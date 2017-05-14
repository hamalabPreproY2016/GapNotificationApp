package com.example.develop.gapnotificationapp.experiment;

import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.model.Face;
import com.example.develop.gapnotificationapp.model.Heartrate;
import com.example.develop.gapnotificationapp.model.ResponseAngry;
import com.example.develop.gapnotificationapp.model.Voice;

/**
 * Created by ragro on 2017/04/16.
 */

public abstract class ExperimentManagerListener {
    public abstract void GetHeartRate(Heartrate data); // 心拍が取得できた時に呼び出される
    public abstract void GetEmg(Emg data);  // 筋電が取得できた時に呼び出される
    public abstract void GetVoice(Voice data); // 音声が取得できた時に呼び出される
    public abstract void GetFace(Face data); // 写真が取得できた時に呼び出される
    public abstract void GetEmgAverage(int average); // 筋電の平均値がAPIサーバから取得できた時に呼び出される
    public abstract void GetAngry(ResponseAngry response); // 怒り判定の結果がAPIサーバから取得できた時に呼び出される
    public abstract void GetEnoughStockHeartRate(); // 十分な心拍値が取得できたら呼び出される
}
