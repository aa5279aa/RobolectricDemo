package com.xt.robolectricdemo.mvp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Build;

import androidx.fragment.app.Fragment;


import com.xt.robolectricdemo.R;
import com.xt.thirdparty.DataAdapaterClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

/**
 * 替换MVPActivity中的MVPPresenter。
 * 针对方法进行单测
 */
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
@RunWith(RobolectricTestRunner.class)
public class MVPActivityTest {

    @Before
    public void init() {

    }

    /**
     * 验证点：
     * 1.getHomeInfo()方法被执行
     * 2.Fragment有展示
     * 3.监听被注册
     * 4.图标有显示
     */
    @Test
    public void testInit() {
        DataAdapaterClient mockClient = mock(DataAdapaterClient.class);
        try (MockedStatic<DataAdapaterClient> ignored2 = mockStatic(DataAdapaterClient.class)) {
            when(DataAdapaterClient.getInstance()).thenReturn(mockClient);
            //创建MVPActivity的控制对象，这里的MVPActivity对象只是创建，没有做任何生命周期操作。
            ActivityController<MVPActivity> controller = Robolectric.buildActivity(MVPActivity.class);
            //创建mock的Presenter
            MVPPresenter mockPresenter = mock(MVPPresenter.class);
            //mock掉Presenter的getHomeInfo()方法，使之不做任何操作。
            Mockito.doAnswer(invocation -> {
                //do nothing
                return null;
            }).when(mockPresenter).requestInfo();
            //执行create生命周期，这时候，原始的Presenter会被创建。
            MVPActivity mainActivity = controller.create().start().get();
            //注入mock的Presenter，替换掉原始的Presenter
            mainActivity.presenter = mockPresenter;
            //执行resume的生命周期，并且显示
            controller.postCreate(null).resume().visible().topActivityResumed(true);

            //因为我们代码中，主线程有post操作，这里则去执行代码中的主线程任务
            ShadowLooper shadowLooper = ShadowLooper.getShadowMainLooper();
            shadowLooper.runToEndOfTasks();

            //这里进行验证，看getHomeInfo()方法是否调用，首次进来，应该只调用1次
            verify(mockPresenter, times(1)).requestInfo();

            //2.Fragment有展示
            Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            assertNotNull(fragment);
            assertTrue(fragment.isVisible());

            //3.监听被注册
            MVPActivity.AdapterListener adapterListener = mainActivity.getAdapterListener();
            assertNotNull(adapterListener);
            //验证是否执行过registerDataNotifyListener方法
            verify(mockClient).registerDataNotifyListener("key", adapterListener);

            //4.验证结束
            controller.destroy();
            verify(mockClient).unRegisterDataNotifyListener(adapterListener);
        }
    }

    @Test
    public void testOnResume() {
        ActivityController<MVPActivity> controller = Robolectric.buildActivity(MVPActivity.class);
        //hook掉Presenter
        MVPPresenter mockPresenter = mock(MVPPresenter.class);
        Mockito.doAnswer(invocation -> {
            //do nothing
            return null;
        }).when(mockPresenter).requestInfo();
        MVPActivity mainActivity = controller.create().start().get();
        mainActivity.presenter = mockPresenter;

        assertFalse(mainActivity.isFirst);
        controller.postCreate(null).resume().visible().topActivityResumed(true);
        //1.这里验证不过，因为调用了2次getHomeInfo，这其实是逻辑问题
        verify(mockPresenter, times(1)).requestInfo();

        controller.resume();
        //再一次调用，增加一次调用，并且isFirst=true
        assertTrue(mainActivity.isFirst);
        verify(mockPresenter, times(2)).requestInfo();
    }

    @Test
    public void testReloadData() {
        ActivityController<MVPActivity> controller = Robolectric.buildActivity(MVPActivity.class);
        //hook掉Presenter
        MVPPresenter mockPresenter = mock(MVPPresenter.class);
        Mockito.doAnswer(invocation -> {
            //do nothing
            return null;
        }).when(mockPresenter).requestInfo();
        MVPActivity mainActivity = controller.create().start().get();
        mainActivity.presenter = mockPresenter;


        controller.postCreate(null).resume().visible().topActivityResumed(true);
        verify(mockPresenter, times(1)).requestInfo();
    }

    /**
     * 上面已覆盖
     */
    @Test
    public void testInitDataListener() {

    }

    /**
     * 上面已覆盖
     */
    @Test
    public void testOnDestroy() {

    }
}