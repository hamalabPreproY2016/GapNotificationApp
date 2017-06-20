package com.example.develop.gapnotificationapp.Ble;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.Objects;

/**
 * Created by ragro on 2017/04/02.
 */

@FunctionalInterface
public interface NotificationListener{
    void getNotification(byte[] bytes);
}
@FunctionalInterface
interface ChangeStateListener{
    void ChangeNotification(RxBleConnection.RxBleConnectionState state);
}