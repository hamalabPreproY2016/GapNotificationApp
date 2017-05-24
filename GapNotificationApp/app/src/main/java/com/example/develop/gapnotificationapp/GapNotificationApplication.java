package com.example.develop.gapnotificationapp;

import android.app.Application;
import android.content.Context;

import com.example.develop.gapnotificationapp.Ble.BleContentManager;
import com.example.develop.gapnotificationapp.Ble.TestBleContent;
import com.example.develop.gapnotificationapp.camera.TakePictureRepeater;
import com.example.develop.gapnotificationapp.rest.RestManager;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

/**
 * Created by ragro on 2017/04/01.
 */

public class GapNotificationApplication extends Application {
    private RxBleClient rxBleClient;
    private BleContentManager bleManager;
    private RestManager restManager;
    private TakePictureRepeater repeater;

    public static final boolean BLE_TEST = false; // テストBLEモジュールを使う
    public static final boolean STOCK_HEART_TEST = false;  // 心拍のストックをテスト値で代用
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

    // RestManagerを取得
    public static RestManager getRestManager(Context context){
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();
        return application.restManager;
    }

    public static TakePictureRepeater getTakePictureRepeater(Context context) {
        GapNotificationApplication application = (GapNotificationApplication) context.getApplicationContext();
        return application.repeater;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        rxBleClient = RxBleClient.create(this);
        bleManager = new BleContentManager();
        RxBleClient.setLogLevel(RxBleLog.DEBUG);
        restManager = new RestManager();
        repeater = new TakePictureRepeater();
        // テストフラグが立っていればのTESTBLEモジュールを使用する
        if (GapNotificationApplication.BLE_TEST){
            TestBleContent heartRate = new TestBleContent();
            bleManager.setHeartRate(heartRate);
            TestBleContent emg = new TestBleContent();
            bleManager.setEMG(emg);
        }
    }
}
