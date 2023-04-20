package com.xt.robolectricdemo.mvp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Handler;

import com.xt.robolectricdemo.mvp.model.InfoModel;
import com.xt.thirdparty.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

@RunWith(RobolectricTestRunner.class)
public class MVPPresenterTest {

    MVPPresenter presenter;
    MVPActivity mock1;

    @Before
    public void init() {
        mock1 = mock(MVPActivity.class);
        MVPPresenter local = new MVPPresenter();
        presenter = spy(local);
        presenter.onAttach(mock1);
    }


    @Test
    public void testRequestInfo() {
        InfoModel accountInfoEntity = new InfoModel();
        accountInfoEntity.status = 200;
        accountInfoEntity.statusDesc = "success";

        Flowable<InfoModel> noNetWorkFlowable = Flowable.create(emitter -> {
            InfoModel baseEntity = new InfoModel();
            accountInfoEntity.status = 200;
            accountInfoEntity.statusDesc = "success";
            emitter.onNext(baseEntity);
        }, BackpressureStrategy.BUFFER);

        DataSource mock = mock(DataSource.class);
        try (MockedStatic<DataSource> ignored = mockStatic(DataSource.class)) {
            when(DataSource.getInstance()).thenReturn(mock);
            when(mock.getDataInfo()).thenReturn(noNetWorkFlowable);
            //这里，为什么线程中的内容没有执行？ 因为没有spy MainPresenter

            //为什么没有执行noNetWorkFlowable中的回调？因为没有mock掉DataSource

            //为什么没有执行getHomeInfo中的回调，但是执行了noNetWorkFlowable中的？因为主线程阻塞，所以无法执行，通过runToEndOfTasks避免阻塞
            presenter.requestInfo();
            new Handler().postDelayed(() -> {
                System.out.println("verify getHomeInfo");
                verify(presenter, times(1)).requestInfo();
            }, 100);
            ShadowLooper shadowLooper = ShadowLooper.getShadowMainLooper();
            shadowLooper.runToEndOfTasks();
        }
    }


    @Test
    public void testProcessInfoAndRefreshPage() {
        InfoModel entity = new InfoModel();
        entity.status = 101;
        entity.statusDesc = "fail1";
        presenter.processInfoAndRefreshPage(entity);
        verify(mock1).refreshPage(true, "fail1");

        entity.status = 200;
        entity.statusDesc = "success";
        presenter.processInfoAndRefreshPage(entity);
        verify(mock1).refreshPage(false, "success");

    }
}


