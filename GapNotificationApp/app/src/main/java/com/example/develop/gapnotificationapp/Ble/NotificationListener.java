package com.example.develop.gapnotificationapp.Ble;

/**
 * Created by ragro on 2017/04/02.
 */

public  abstract class NotificationListener{
    public abstract void getNotification(byte[] bytes);
}