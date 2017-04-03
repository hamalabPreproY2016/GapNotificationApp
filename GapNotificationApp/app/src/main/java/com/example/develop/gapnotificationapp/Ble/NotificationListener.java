package com.example.develop.gapnotificationapp.Ble;

import java.util.Objects;

/**
 * Created by ragro on 2017/04/02.
 */

public  abstract class NotificationListener{
    public Object object;
    public abstract void getNotification(byte[] bytes);
}