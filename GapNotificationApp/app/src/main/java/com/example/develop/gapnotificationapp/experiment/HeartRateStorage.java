package com.example.develop.gapnotificationapp.experiment;

import android.content.Context;

import com.example.develop.gapnotificationapp.GapNotificationApplication;
import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.model.Heartrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ragro on 2017/05/08.
 */

public class HeartRateStorage {
    // 取得する心拍値の数
    private final int HEART_RATE_SIZE = 256;
    private long _startTime = 0; // 開始時刻
    private List<Heartrate> _heartRateData; // 心拍データ
    private Context _context;

    private HeartRateStorageListener _listener = null; // リスナー
    public HeartRateStorage(Context context) {
        _context = context;
    }
    public void Start(){
        // 心拍リストを初期化
        _heartRateData = new ArrayList<>();
        // 開始時刻を保持
        _startTime = System.currentTimeMillis();
        // テストデータ
        createTestMoule();
        // 心拍リスナーをセット
//        GapNotificationApplication.getBleContentManager(_context).getHeartRate().setNotificationListener(new NotificationListener() {
//            @Override
//            public void getNotification(byte[] bytes) {
//                Short data = (short) BinaryInteger.TwoByteToInteger(bytes);
//                setHeartRateCache(data);
//            }
//        });
    }
    public void SetHeartRateListener(HeartRateStorageListener listener){
        _listener = listener;
    }

    private void SetHeartRate(Short data) {
        // 取得予定の個数まで手に入った
        if (_heartRateData.size() == HEART_RATE_SIZE){
            // 完了通知
            if (_listener != null) _listener.Completed();
            // 心拍リスナーを解除
//            GapNotificationApplication.getBleContentManager(_context).geHeartRate().setNotificationListener(null);
            return;
        }
        Heartrate heart = new Heartrate();
        heart.time = Long.toString(getRemmaningTime());
        heart.value = data.intValue();
        // 心拍データリストにデータを追加
        _heartRateData.add(heart);
        if (_listener != null) {
            _listener.GetHeartRate(heart);
        }
    }
    private long getRemmaningTime(){return System.currentTimeMillis() - _startTime;}

    private void createTestMoule(){
        int COUNT = 300;
        for(int i = 0; i < COUNT ; i ++){
            // 筋電の値を乱数で作成
            Random r = new Random();
            Short value = (short)(r.nextInt(300) + 1);
            SetHeartRate(value);
        }

    }

    public int GetSize(){
        return _heartRateData.size();
    }
    public int GetMaxSize(){
        return HEART_RATE_SIZE;
    }
    public List<Heartrate> GetHeartRate(){
        return  _heartRateData;
    }
}
