package com.example.develop.gapnotificationapp.Ble;

import android.util.Log;

import com.example.develop.gapnotificationapp.model.ResponseAngry;
import com.example.develop.gapnotificationapp.util.BinaryInteger;
import com.example.develop.gapnotificationapp.util.HexString;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by ragro on 2017/05/10.
 */

public class TestBleContent  extends BleContent{
    private Timer timer;
    // コンストラクタ
    public TestBleContent(){
        super();
    }
    @Override
    public void Connect(){
        timer = new Timer(true);
        // 1秒ごとに筋電のテストデータを作成
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 筋電の値を乱数で作成
                Random r = new Random();
                short value = (short)(r.nextInt(300) + 1);
                if (_listener != null) {
                    _listener.getNotification(BinaryInteger.ShortToByte(value));
                }
            }
        }, 0, 1000);
    }

}
