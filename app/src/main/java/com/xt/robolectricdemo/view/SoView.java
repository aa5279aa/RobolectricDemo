package com.xt.robolectricdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.xt.client.jni.Java2CJNI;

/**
 * the view use so
 */
public class SoView extends View {

    public SoView(Context context) {
        this(context, null);
    }

    public SoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //this use jni
        Java2CJNI java2CJNI = new Java2CJNI();
        Log.i("SoView", java2CJNI.java2C());
    }
}
