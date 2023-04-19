package com.xt.robolectricdemo.mvp;

import com.xt.thirdparty.DataSource;
import com.xt.robolectricdemo.mvp.model.InfoModel;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MVPPresenter implements IMVPActivityContract.IMainActivityPresenter {
    IMVPActivityContract.IMainActivityView mView;

    public void onAttach(IMVPActivityContract.IMainActivityView view) {
        this.mView = view;
    }

    @Override
    public void requestInfo() {
        //请求数据，订阅，并显示
        Consumer<InfoModel> consumer = this::processInfoAndRefreshPage;
        Flowable<InfoModel> observable = DataSource.getInstance().getDataInfo();
        Disposable disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * action
     */
    public void processInfoAndRefreshPage(InfoModel infoModel) {
        int status = infoModel.status;
        String statusDesc = infoModel.statusDesc;
        mView.refreshPage(status != 200, statusDesc);
    }

}
