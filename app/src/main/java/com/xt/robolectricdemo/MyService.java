package com.xt.robolectricdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xt.robolectricdemo.mvp.MVPActivity;

public class MyService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            int i = 0;
            while (i++ < 5) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("lxltest", "time:" + i);
            }
            Log.i("lxltest", "startActivity");
            Intent intent1 = new Intent(getBaseContext(), MVPActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);

        }).start();


        return super.onStartCommand(intent, flags, startId);
    }
}
