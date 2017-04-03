package com.example.develop.gapnotificationapp.Ble;

import android.content.Context;

import com.example.develop.gapnotificationapp.GapNotificationApplication;

/**
 * Created by ragro on 2017/04/03.
 */

public class BleContentManager {
    private BleContent _HeartRate;
    private BleContent _EMG;
    public static final int HEART_RATE = 0;
    public static final int EMG = 1;
    public static final int UNREGISTERED = -1;

    // 心拍
    public void setHeartRate(BleContent ble){
        _HeartRate = ble;
    }
    public BleContent getHeartRate(BleContent ble){
        return _HeartRate;
    }
    // 筋電位
    public void setEMG(BleContent ble){
        _EMG = ble;
    }
    public BleContent getEMG(BleContent ble){
        return _EMG;
    }

    // 渡されたBleが心拍として登録されていたら0,筋電位だったら1,未登録なら-1
    public int isRegistered(BleContent ble){
        if (_HeartRate != null && _HeartRate.getDevice().equals(ble.getDevice())){
            return HEART_RATE;
        }
        if (_EMG != null && _EMG.getDevice().equals(ble.getDevice())){
            return EMG;
        }

        return UNREGISTERED;
    }
    // 登録の解除
    public void Deregistration( BleContent ble){

        switch (isRegistered(ble)){
            case HEART_RATE:
                _HeartRate = null;
                break;
            case EMG:
                _EMG = null;
                break;
        }
    }

}
