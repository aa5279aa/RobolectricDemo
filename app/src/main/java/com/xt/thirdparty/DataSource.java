package com.xt.thirdparty;

import android.util.Log;

import com.xt.client.jni.Java2CJNI;
import com.xt.robolectricdemo.mvp.model.InfoModel;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class DataSource {

    public static DataSource getInstance() {
        return DataSource.SingletonHolder.SINGLETON;
    }

    private static class SingletonHolder {
        private static final DataSource SINGLETON = new DataSource();

        private SingletonHolder() {
        }
    }

    /**
     * reqeust server,and need mock this method.
     *
     * @return
     */
    public Flowable<InfoModel> getDataInfo() {
        //this us jni
        Java2CJNI java2CJNI = new Java2CJNI();
        Log.i("SoView", java2CJNI.java2C());
        return Flowable.create(emitter -> {
            InfoModel infoModel = new InfoModel();
            infoModel.status = 100;
            infoModel.statusDesc = "fail";
            emitter.onNext(infoModel);
        }, BackpressureStrategy.BUFFER);
    }
}
