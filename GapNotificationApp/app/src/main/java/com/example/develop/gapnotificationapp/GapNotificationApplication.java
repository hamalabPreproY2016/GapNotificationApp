package com.example.develop.gapnotificationapp;

import android.app.Application;
import android.content.Context;

import com.example.develop.gapnotificationapp.Ble.BleContent;
import com.example.develop.gapnotificationapp.Ble.BleContentManager;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

/**
 * Created by ragro on 2017/04/01.
 */

public class GapNotificationApplication extends Application {
    private RxBleClient rxBleClient;
    private BleContentManager bleManager;
    /**
     * In practise you will use some kind of dependency injection pattern.
     */
    // RxBleClientを取得
    public static RxBleClient getRxBleClient(Context context) {
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();

        return application.rxBleClient;
    }
    // BleContentManagerを取得
    public static BleContentManager getBleContentManager(Context context) {
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();

        return application.bleManager;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        rxBleClient = RxBleClient.create(this);
        bleManager = new BleContentManager();
        RxBleClient.setLogLevel(RxBleLog.DEBUG);
    }
}
