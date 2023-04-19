package com.xt.robolectricdemo.mock;

import android.content.Context;
import android.util.AttributeSet;

import org.junit.Before;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowView;

@Implements(com.xt.robolectricdemo.view.SoView.class)
public class SoViewMock extends ShadowView {

    @Before
    public void setUp() {

    }

    @Implementation
    public void __constructor__(Context context) {
        __constructor__(context, null);
    }

    @Implementation
    public void __constructor__(Context context, AttributeSet attrs) {
    }


    @Implementation
    protected void onAttachedToWindow() {

    }

    @Implementation
    protected void onDetachedFromWindow() {

    }

    @Implementation
    public void stop() {

    }
}
