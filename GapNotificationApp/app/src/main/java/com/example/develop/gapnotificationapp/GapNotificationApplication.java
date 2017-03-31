package com.example.develop.gapnotificationapp;

import android.app.Application;
import android.content.Context;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

/**
 * Created by ragro on 2017/04/01.
 */

public class GapNotificationApplication extends Application {
    private RxBleClient rxBleClient;

    /**
     * In practise you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getRxBleClient(Context context) {
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();

        return application.rxBleClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rxBleClient = RxBleClient.create(this);
        RxBleClient.setLogLevel(RxBleLog.DEBUG);
    }
}
