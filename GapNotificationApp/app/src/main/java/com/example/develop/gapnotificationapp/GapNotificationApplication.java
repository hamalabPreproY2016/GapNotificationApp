package com.example.develop.gapnotificationapp;

import android.app.Application;
import android.content.Context;

import com.example.develop.gapnotificationapp.Ble.BleContent;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

/**
 * Created by ragro on 2017/04/01.
 */

public class GapNotificationApplication extends Application {
    private RxBleClient rxBleClient;
    private BleContent _HeartRate;
    private BleContent _EMG;

    /**
     * In practise you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getRxBleClient(Context context) {
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();

        return application.rxBleClient;
    }
    // 心拍
    public static void setHeartRate(Context context, BleContent ble){
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();
        application._HeartRate = ble;
    }
    public static BleContent getHeartRate(Context context, BleContent ble){
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();
        return application._HeartRate;
    }
    // 筋電位
    public static void setEMG(Context context, BleContent ble){
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();
        application._EMG = ble;
    }
    public static BleContent getEMG(Context context, BleContent ble){
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();
        return application._EMG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rxBleClient = RxBleClient.create(this);
        RxBleClient.setLogLevel(RxBleLog.DEBUG);
    }
}
