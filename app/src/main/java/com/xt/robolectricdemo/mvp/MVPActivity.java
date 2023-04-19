package com.xt.robolectricdemo.mvp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.xt.robolectricdemo.R;
import com.xt.thirdparty.DataAdapaterClient;

public class MVPActivity extends FragmentActivity implements IMVPActivityContract.IMainActivityView, View.OnClickListener {

    MVPPresenter presenter;
    ImageView imgeView;
    TextView textDesc;
    boolean isFirst;
    AdapterListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        init();
    }

    private void initView() {
        imgeView = findViewById(R.id.image_view);
        textDesc = findViewById(R.id.text_desc);
        presenter = new MVPPresenter();
        presenter.onAttach(this);
        imgeView.setOnClickListener(this);
    }


    private void initListener() {
        if (listener == null) {
            listener = new AdapterListener();
            DataAdapaterClient.getInstance().registerDataNotifyListener("key", listener);
        }
    }

    private void init() {
        new Handler().post(() -> {
            //refresh Activity page
            presenter.requestInfo();

            //show fragment
            MVPFragment fragment = new MVPFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commitAllowingStateLoss();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //If the first time,refresh data
        if (isFirst) {
            presenter.requestInfo();
        } else {
            isFirst = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            DataAdapaterClient.getInstance().unRegisterDataNotifyListener(listener);
        }
    }

    @Override
    public void refreshPage(boolean isShow, String message) {
        imgeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        textDesc.setText(message);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("title");
        builder.setMessage("this is detail message!");
        builder.create().show();
    }

    public AdapterListener getAdapterListener() {
        return listener;
    }


    public class AdapterListener implements DataAdapaterClient.DataChangedListener {

        @Override
        public void onDataChanged(String var1, String var2) {
            //do somethind
        }
    }

}
