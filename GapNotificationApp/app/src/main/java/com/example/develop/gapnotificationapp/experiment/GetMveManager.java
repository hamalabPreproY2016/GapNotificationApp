package com.example.develop.gapnotificationapp.experiment;

import android.util.Log;

import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAdvance.request.RequestPrepareEMG;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAdvance.response.ResponseMVE;
import com.example.develop.gapnotificationapp.rest.RestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ragro on 2017/05/08.
 */

public class GetMveManager {
    private int _mve; // MVE
    private long _interval = 10000; // 10秒間取得する
    private List<Emg> _emgData = new ArrayList<>(); // 筋電データ
    private RestManager _restManager = new RestManager(); // RestAPIを管理する
    private static final String TAG = "experiment";
    private GetMveManagerListener _listener = null;
    private long _startTime;

    private void createTestMoule(){
        int COUNT = 300;
        for(int i = 0; i < COUNT ; i ++){
            // 筋電の値を乱数で作成
            Random r = new Random();
            Short value = (short)(r.nextInt(300) + 1);
            setEmgData(value);
        }

    }

    // Mveの取得を開始
    public void Start(){
        _startTime = getRemmaningTime();
//        // 筋電リスナーをセット
//        GapNotificationApplication.getBleContentManager(_context).getEMG().setNotificationListener(new NotificationListener() {
//            @Override
//            public void getNotification(byte[] bytes) {
//                Short data = (short) BinaryInteger.TwoByteToInteger(bytes);
//            }
//        });
        // テストデータを作成
        createTestMoule();
        // 10秒後にAPIへ筋電を送る
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RequestPrepareEMG request_average = new RequestPrepareEMG();
                request_average.emg = new ArrayList<>(_emgData);
                // 筋電をAPIに送って平均値を取得する
                _restManager.postEmgMVE(request_average, new Callback<ResponseMVE>() {
                    @Override
                    public void onResponse(Call<ResponseMVE> call, Response<ResponseMVE> response) {
                        if (response.code() == 200) {
                            Log.d(TAG, "mve :" + Double.toString(response.body().mve));
                            _mve = response.body().mve;
                            if(_listener != null) {
                                _listener.getMve(_mve);
                            }
                        } else {
                            Log.d(TAG, "access error : status code " + Integer.toString(response.code()));
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseMVE> call, Throwable t) {

                    }
                });
            }
            }, _interval);
    }
    public int getMve(){
        return _mve;
    }
    // 筋電データを一時的に記憶
    private void setEmgData(Short data) {
        Emg emg = new Emg();
        emg.time = Long.toString(getRemmaningTime());
        emg.value = data.intValue();
        Log.d(TAG, "data : " + emg.time + ", "+ Integer.toString(emg.value));
        // 筋電データリストにデータを追加
        _emgData.add(emg);
    }
    // 実験開始からの経過時間を取得する
    private long getRemmaningTime(){return System.currentTimeMillis() - _startTime;}
    public void setListener(GetMveManagerListener listener){_listener = listener;}

}
