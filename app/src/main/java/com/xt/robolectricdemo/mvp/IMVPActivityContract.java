package com.xt.robolectricdemo.mvp;

public class IMVPActivityContract {

    interface IMainActivityPresenter {
        /**
         * 请求数据
         */
        void requestInfo();

    }

    interface IMainActivityView {
        /**
         * 刷新回调
         */
        void refreshPage(boolean isShow, String messag);
    }
}
