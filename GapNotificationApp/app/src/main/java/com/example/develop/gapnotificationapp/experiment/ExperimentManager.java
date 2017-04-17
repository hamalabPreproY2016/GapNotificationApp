package com.example.develop.gapnotificationapp.experiment;

import android.content.Context;
import android.util.Log;

import com.example.develop.gapnotificationapp.Log.GapFileManager;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAverage.request.RequestAverage;
import com.example.develop.gapnotificationapp.GapNotificationApplication;
import com.example.develop.gapnotificationapp.Log.GapFileManager;
import com.example.develop.gapnotificationapp.camera.Camera;
import com.example.develop.gapnotificationapp.rest.RestManager;
import com.example.develop.gapnotificationapp.voice.RealTimeVoiceSlicer;
import com.example.develop.gapnotificationapp.voice.VoiceSliceListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ragro on 2017/04/12.
 */

public class ExperimentManager {
    public enum Device {VOICE, FACE, HEARTRATE, EMG};

    private static final String TAG = "experiment";
    private Context _context;
    private boolean[] _flag = new boolean[4]; // 各デバイスのデータが揃っているのかの管理
    private File _rootDirectory; // 実験ログを保存するルートディレクトリ
    private RestManager _restManager; // RestAPIを管理する
    private SensorStruct _sensor = new SensorStruct();
    private ExperimentManagerListener _listener = null;

    // 筋電平均取得時間関係
    private final long _average_time_milliseconds = 60 * 5 * 1000; // 5分間の平均取得時間
    private boolean _isGetAverageEmgSession;

    private List<SensorStruct.VoiceStruct> _voiceData = new ArrayList<SensorStruct.VoiceStruct>(); // 音声データ
    private List<SensorStruct.FaceStruct> _faceData = new ArrayList<SensorStruct.FaceStruct>(); // カメラデータ
    private List<SensorStruct.HeartRateStruct> _heartRateData = new ArrayList<SensorStruct.HeartRateStruct>(); // 心拍データ
    private List<SensorStruct.EmgStruct> _emgData = new ArrayList<SensorStruct.EmgStruct>(); // 筋電データ

    private int[] _cacheStartIndexList = new int[4]; // 各センサーのキャッシュIndex

    // 時間関係
    private List<Long> _sessionTime = new ArrayList<Long>(); // セッションタイム
    private long _startTime; // 実験開始時間

    // 音声
    private RealTimeVoiceSlicer _voiceSilcer;



    private GapFileManager _fileManager;

    public ExperimentManager(Context context){
        _context = context;
        _fileManager = new GapFileManager(_context);
        _isGetAverageEmgSession = true;
    }

    // 実験開始
    public void Start(){
        // 実験開始時間を保存
        _startTime = System.currentTimeMillis();
        // キャッシュのIndexを0
        Arrays.fill(_cacheStartIndexList, 0);
        // 実験ディレクトリを取得する
        _rootDirectory = _fileManager.getNewLogDirectory();
        Log.d(TAG, _rootDirectory.toString());

        // 音声保存をするスライサーを作成
        _voiceSilcer = new RealTimeVoiceSlicer(_context);
        // 音声リスナーをセット
        _voiceSilcer.setVoiceSliceListener(new VoiceSliceListener() {
            @Override
            public void Recorded(File file) {
                // 作成されたファイルをキャッシュに保存
                setVoiceCache(file);
            }
        });
        // 音声のスライスをスタート
        _voiceSilcer.Start(_rootDirectory.toString());


        // カメラリスナーをセット
        GapNotificationApplication.getTakePictureRepeater(_context).startCapturePicture(_rootDirectory, new Camera.SaveImageListener() {
            @Override
            public void OnSaveImageComplete(File file) {
                Log.d("saveFile", "save file completed");
                setCameraCache(file);
            }
        });

//
//        // 心拍リスナーをセット
//        GapNotificationApplication.getBleContentManager(_context).getHeartRate().setNotificationListener(new NotificationListener() {
//            @Override
//            public void getNotification(byte[] bytes) {
//                Short data = (short) BinaryInteger.TwoByteToInteger(bytes);
//                setHeartRateCache(data);
//            }
//        });
//
//        // 筋電リスナーをセット
//        GapNotificationApplication.getBleContentManager(_context).getEMG().setNotificationListener(new NotificationListener() {
//            @Override
//            public void getNotification(byte[] bytes) {
//                Short data = (short) BinaryInteger.TwoByteToInteger(bytes);
//                setEmgCache(data);
//            }
//        });
    }

    // 実験終了
    public void Finish(){
        // 保存する
        // リスナーとか解除する
    }

    // セッションを追加
    public void Session(){
    }

    // 音声データを一時的に記憶
    private void setVoiceCache(File data){
        long current_time = getRemmaningTime();
        // 音声データリストにデータを追加
        _voiceData.add(_sensor.new VoiceStruct(data, current_time));
        _flag[Device.VOICE.ordinal()] = true;
        // リスナーを呼び出し
        if (_listener != null) {
            _listener.GetVoice(_sensor.new VoiceStruct(data, current_time));
        }
        sendApiServer();
    }

    // 写真データを一時的に記憶
    private void setCameraCache(File data){
        long current_time = getRemmaningTime();
        // 写真データリストにデータを追加
        _faceData.add(_sensor.new FaceStruct(data, current_time));
        _flag[Device.FACE.ordinal()] = true;
        // リスナーを呼び出し
        if (_listener != null) {
            _listener.GetFace(_sensor.new FaceStruct(data, current_time));
        }
        sendApiServer();
    }

    // 心拍データを一時的に記憶
    private void setHeartRateCache(Short data){
        long current_time = getRemmaningTime();
        // 心拍データリストにデータを追加
        _heartRateData.add(_sensor.new HeartRateStruct(data, current_time));
        _flag[Device.HEARTRATE.ordinal()] = true;
        if (_listener != null) {
            _listener.GetHeartRate(_sensor.new HeartRateStruct(data, current_time));
        }
        sendApiServer();
    }

    // 筋電データを一時的に記憶
    private void setEmgCache(Short data) {
        long current_time = getRemmaningTime();
        // 筋電データリストにデータを追加
        _emgData.add(_sensor.new EmgStruct(data, current_time));
        _flag[Device.EMG.ordinal()] = true;
        if (_listener != null) {
            _listener.GetEmg(_sensor.new EmgStruct(data, current_time));
        }
        sendApiServer();
    }

    // 全てのデータが一つ以上キャッシュされているかどうか
    private boolean isAllCompleted(){
        for(boolean result : _flag){
            if (result == false){
                return false;
            }
        }
        return true;
    }

    private void cacheClear(){
        // キャッシュフラグ配列を全てfalseにする
        Arrays.fill(_flag, false);
        // 各デバイスのキャッシュを削除
    }
    // キャッシュデータをAPIserverに送る
    private void sendApiServer(){

        // 平常値取得セッションの時はその処理を行う
        if (_isGetAverageEmgSession){
            SessionAverageEMG();
            return ;
        }

        // 全てのデータがキャッシュされていない場合は送らない
        if (!isAllCompleted()) return;

        // 各センサーのキャッシュを取り出す
        List<SensorStruct.VoiceStruct> voice_cache =
                new ArrayList<>(_voiceData.subList(_cacheStartIndexList[Device.VOICE.ordinal()], _voiceData.size() )); // 音声データ
        List<SensorStruct.FaceStruct> face_cache =
                new ArrayList<>(_faceData.subList(_cacheStartIndexList[Device.FACE.ordinal()], _faceData.size() )); // 顔写真データ
        List<SensorStruct.EmgStruct> emg_cache =
                new ArrayList<>(_emgData.subList(_cacheStartIndexList[Device.EMG.ordinal()], _emgData.size() )); // 筋電データ
        List<SensorStruct.HeartRateStruct> heartrate_cache =
                new ArrayList<>(_heartRateData.subList(_cacheStartIndexList[Device.HEARTRATE.ordinal()] ,_heartRateData.size() )); // 心拍データ

        // Requestデータを作る

        // 全てのキャッシュを削除
        cacheClear();

        // ネットワークに送信する

    }
    // 実験開始からの経過時間を取得する
    private long getRemmaningTime(){return System.currentTimeMillis() - _startTime;}

    // 筋電の平均を取得
    private void SessionAverageEMG(){
        // 現在の経過時間が平常値取得時間よりも長かった場合は
        if (getRemmaningTime() > _average_time_milliseconds){
            RequestAverage request_average = new RequestAverage();
            request_average.emg = new ArrayList<>();

//_restManager.postEmgAverage();
        }
    }

}
