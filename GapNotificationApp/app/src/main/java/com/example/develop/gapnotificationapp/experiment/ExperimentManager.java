package com.example.develop.gapnotificationapp.experiment;

import android.content.Context;
import android.util.Log;

import com.example.develop.gapnotificationapp.Ble.BleContentManager;
import com.example.develop.gapnotificationapp.Ble.TestBleContent;
import com.example.develop.gapnotificationapp.CSVManager;
import com.example.develop.gapnotificationapp.GapNotificationApplication;
import com.example.develop.gapnotificationapp.Log.GapFileManager;
import com.example.develop.gapnotificationapp.camera.Camera;
import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.model.Face;
import com.example.develop.gapnotificationapp.model.Heartrate;
import com.example.develop.gapnotificationapp.model.ResponseAngry;
import com.example.develop.gapnotificationapp.model.Session;
import com.example.develop.gapnotificationapp.model.Voice;
import com.example.develop.gapnotificationapp.model.mve;
import com.example.develop.gapnotificationapp.rest.Pojo.Angry.request.RequestAngry;
import com.example.develop.gapnotificationapp.rest.RestManager;
import com.example.develop.gapnotificationapp.util.BinaryInteger;
import com.example.develop.gapnotificationapp.voice.RealTimeVoiceSlicer;
import com.example.develop.gapnotificationapp.voice.VoiceSliceListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ragro on 2017/04/12.
 */

public class ExperimentManager {

    private final CSVManager mveCSVManager;
    private final CSVManager sessionCSVManager;
    private final CSVManager responseCSVManager;
    private final CSVManager emgCSVManager;
    private final CSVManager voiceCSVManager;
    private final CSVManager heartrateCSVManager;
    private final CSVManager faceCSVManager;

    public enum Device {VOICE, FACE, HEARTRATE, EMG};

    private static final String TAG = "experiment";
    private Context _context;
    private boolean[] _flag = new boolean[4]; // 各デバイスのデータが揃っているのかの管理
    private File _rootDirectory; // 実験ログを保存するルートディレクトリ
    private RestManager _restManager; // RestAPIを管理する
    private ExperimentManagerListener _listener = null;
    private mve _MVE = new mve();
    public static final int STOCK_HEARTRATE_SIZE = 256;

    private Voice _voiceData; // 音声データ
    private Face _faceData; // カメラデータ
    private List<Heartrate> _heartRateData = new ArrayList<>(); // 心拍データ
    private List<Emg> _emgData = new ArrayList<>(); // 筋電データ
    private int _nextSendBeginEMG; // 次に送る筋電データリストの先頭Index

    private List<ResponseAngry> _angryData = new ArrayList<>();

    // 時間関係
    private List<Session> _sessionTime = new ArrayList<Session>(); // セッションタイム
    private long _startTime; // 実験開始時間

    // 音声
    private RealTimeVoiceSlicer _voiceSilcer;

    private GapFileManager _fileManager;

    // BLEデバイス
    private BleContentManager _bleManager;

    public ExperimentManager(Context context){
        _context = context;
        _fileManager = new GapFileManager(_context);
        _restManager = new RestManager();
        _startTime = -1;
        _bleManager = GapNotificationApplication.getBleContentManager(_context);
        _MVE.value = -1;

        // 実験ディレクトリを取得する
        _rootDirectory = _fileManager.getNewLogDirectory();
        Log.d(TAG, _rootDirectory.toString());

        File csvDir = CSVManager.createCSVDirectory(_rootDirectory);

        faceCSVManager = new CSVManager(new File(csvDir, "face.csv"));

        heartrateCSVManager = new CSVManager(new File(csvDir, "heartrate.csv"));

        voiceCSVManager = new CSVManager(new File(csvDir, "voice.csv"));

        emgCSVManager = new CSVManager(new File(csvDir, "emg.csv"));

        responseCSVManager = new CSVManager(new File(csvDir, "responseAngry.csv"));

        sessionCSVManager = new CSVManager(new File(csvDir, "session.csv"));

        mveCSVManager = new CSVManager(new File(csvDir, "mve.csv"));
    }
    // 心拍ストック開始
    public boolean StartStockHeart(){
        // 心拍bleがセットされていない場合は開始することができない
        if(_bleManager.getHeartRate() == null) return false;
        // 心拍bleの通信開始
        _bleManager.getHeartRate().Connect();


       // 心拍リスナーをセット
        _bleManager.getHeartRate().setNotificationListener(bytes->{
            Short data = (short) BinaryInteger.TwoByteToInteger(bytes);
            setHeartRateCache(data);
            // 十分ストックが貯まったら通知する
            if (_heartRateData.size() > STOCK_HEARTRATE_SIZE && _listener != null){
                _listener.GetEnoughStockHeartRate();
            }
        });
        // 実験開始時間をセット
        _startTime = System.currentTimeMillis();

        // テストフラグが立っていれば、心拍ストックを乱数で作成
        if (GapNotificationApplication.STOCK_HEART_TEST){
            Random r = new Random();
            for (int i = 0; i < STOCK_HEARTRATE_SIZE; i ++){
                short value = (short)(r.nextInt(300) + 1);
                setHeartRateCache(value);
            }
        }

        return true;
    }
    // 現在の心拍値数
    public int GetHeartRateSize(){
        return  _heartRateData.size();
    }
    // リスナーをセット
    public void SetListener(ExperimentManagerListener listener){
        _listener = listener;
    }
    // 実験開始
    public boolean Start(){
        // MVEと心拍のストックが無い場合はスタートしない
        if (!CanStart()) return false;

        // テスト用のBLEContentモジュールを使用する
        if (GapNotificationApplication.BLE_TEST){
            TestBleContent emg = new TestBleContent();
            _bleManager.setEMG(emg);
        }
        // BLEContentがセットされていない場合は開始しない
        if (_bleManager.getEMG() == null || _bleManager.getHeartRate() == null)return false;

        // Bluetooth通信を開始
        _bleManager.getEMG().Connect();
        _bleManager.getHeartRate().Connect();

        // 実験開始時間をセット
        long epoch = getRemmaningTime();

        // 実験開始以前の心拍データの取得時間を実験開始時刻をエポックタイムとしたマイナス値にする
        for(int i = 0; i < _heartRateData.size(); i++){
            long past =  Long.parseLong(_heartRateData.get(i).time);
            _heartRateData.get(i).time = Long.toString(past - epoch);
        }
        // 現在時刻を実験開始時刻にセット
        _startTime = System.currentTimeMillis();

        // 送信する筋電データリストの先頭インデクスを初期化
        _nextSendBeginEMG = 0;

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
        _bleManager.getHeartRate().setNotificationListener(bytes -> {
            Short data = (short) BinaryInteger.TwoByteToInteger(bytes);
            setHeartRateCache(data);
        });

//        // 筋電リスナーをセット
        _bleManager.getEMG().setNotificationListener(bytes->{
            Short data = (short) BinaryInteger.TwoByteToInteger(bytes);
            setEmgCache(data);
        });

        mveCSVManager.csvWriteForLine(_MVE);

        return true;
    }

    // 実験終了
    public void Finish() {
        // 保存する
        // リスナーや他のデバイスを解除する
        _voiceSilcer.Finish();
        _bleManager.getHeartRate().setNotificationListener(null);
        _bleManager.getEMG().setNotificationListener(null);
        GapNotificationApplication.getTakePictureRepeater(_context).invalidateCapturePicture();

//        File csvDir = CSVManager.createCSVDirectory(_rootDirectory);
//
//        CSVManager faceCSVManager = new CSVManager(new File(csvDir, "face.csv"));
//        faceCSVManager.csvWrit
//
// e(_faceData);
//
//        CSVManager heartrateCSVManager = new CSVManager(new File(csvDir, "heartrate.csv"));
//        heartrateCSVManager.csvWrite(_heartRateData);
//
//        CSVManager voiceCSVManager = new CSVManager(new File(csvDir, "voice.csv"));
//        voiceCSVManager.csvWrite(_voiceData);
//
//        CSVManager emgCSVManager = new CSVManager(new File(csvDir, "emg.csv"));
//        emgCSVManager.csvWrite(_emgData);
//
//        CSVManager responseCSVManager = new CSVManager(new File(csvDir, "responseAngry.csv"));
//        responseCSVManager.csvWrite(_angryData);
//
//        CSVManager sessionCSVManager = new CSVManager(new File(csvDir, "session.csv"));
//        sessionCSVManager.csvWrite(_sessionTime);
//
//        CSVManager mveCSVManager = new CSVManager(new File(csvDir, "mve.csv"));
    }

    // セッションを追加
    public void Session(){
        _sessionTime.add(new Session(getRemmaningTime()));
        sessionCSVManager.csvWriteForLine(new Session(getRemmaningTime()));
    }

    // 音声データを一時的に記憶
    private void setVoiceCache(File data){
        Voice voice = new Voice();
        voice.file = data;
        voice.time = Long.toString(getRemmaningTime());
        long current_time = getRemmaningTime();
        // 音声データリストにデータを追加
        _voiceData = voice;
        voiceCSVManager.csvWriteForLine(voice);
        _flag[Device.VOICE.ordinal()] = true;
        // リスナーを呼び出し
        if (_listener != null) {
            _listener.GetVoice(voice);
        }
        sendApiServer();
    }

    // 写真データを一時的に記憶
    private void setCameraCache(File data){
        Face face = new Face();
        face.file = data;
        face.time = Long.toString(getRemmaningTime());
        // 写真データリストにデータを追加
        _faceData = face;
        faceCSVManager.csvWriteForLine(face);
        _flag[Device.FACE.ordinal()] = true;
        // リスナーを呼び出し
        if (_listener != null) {
            _listener.GetFace(face);
        }
        sendApiServer();
    }

    // 心拍データを一時的に記憶
    private void setHeartRateCache(Short data){
        Heartrate heart = new Heartrate();
        heart.time = Long.toString(getRemmaningTime());
        heart.value = data.intValue();
        // 心拍データリストにデータを追加
        _heartRateData.add(heart);
        heartrateCSVManager.csvWriteForLine(heart);
        _flag[Device.HEARTRATE.ordinal()] = true;
        if (_listener != null) {
            _listener.GetHeartRate(heart);
        }
        Log.d(TAG, "get heart rate : " + heart.value.toString() + " time : " + heart.time);
        sendApiServer();
    }

    // 筋電データを一時的に記憶
    private void setEmgCache(Short data) {
        Emg emg = new Emg();
        emg.time = Long.toString(getRemmaningTime());
        emg.value = data.intValue();
        Log.d(TAG, "data : " + emg.time + ", "+ Integer.toString(emg.value));
        // 筋電データリストにデータを追加
        _emgData.add(emg);
        emgCSVManager.csvWriteForLine(emg);
        _flag[Device.EMG.ordinal()] = true;
        if (_listener != null) {
            _listener.GetEmg(emg);
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

        // 全てのデータがキャッシュされていない場合は送らない
        if (!isAllCompleted()) return;


        // 心拍のデータが256個揃っていない場合は送らない
        if(_heartRateData.size() < 256) return;


        /* 送信データ作成 */
        RequestAngry sendJson = new RequestAngry();
        sendJson.sendTime = Long.toString(getRemmaningTime()); // 送信時間
        sendJson.emgMve = _MVE.value; // 筋電の平均

        // 心拍の送信データの作成
        // 過去256個のデータを取得
        int dataBegin = _heartRateData.size() - 256;
        sendJson.heartrate  = new ArrayList<>(_heartRateData.subList(dataBegin, _heartRateData.size()));

        // 筋電の送信データの作成
        sendJson.emg= new ArrayList<>(_emgData.subList(_nextSendBeginEMG, _emgData.size()));

//        // 全てのキャッシュを削除
        cacheClear();
        // ネットワークに送信する
        _restManager.postAngry(sendJson,
                _voiceData.file.toString(),
                _faceData.file.toString(),
                new Callback<ResponseAngry>() {
                    @Override
                    public void onResponse(Call<ResponseAngry> call, Response<ResponseAngry> response) {
                        // Statusコード200番の時にリスナーイベントを走らせる
                        if (response.code() == 200) {
                            _angryData.add(response.body());
                            responseCSVManager.csvWriteForLine(response.body());
                            if(_listener != null) {
                                _listener.GetAngry(response.body());
                            }
                            Log.d(TAG, "access : 200 OK ");
                        } else {
                            Log.d(TAG, "access error : status code " + Integer.toString(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseAngry> call, Throwable t) {
                        Log.d(TAG, "failed:" + t);
                    }
                });


    }
    // 実験開始からの経過時間を取得する
    private long getRemmaningTime(){return System.currentTimeMillis() - _startTime;}

    public void SetHeartRate( List<Heartrate> arr){
        _heartRateData = new ArrayList<>(arr);
    }
    public void SetMve(int mve){
        _MVE.value = mve;
        _MVE.time = "0";
    }
    public boolean CanStart(){
        return _MVE.value != -1 && _heartRateData.size() >= STOCK_HEARTRATE_SIZE;
    }
    // 実験IDを取得
    public String GetExpID(){
        return _rootDirectory.getName();
    }

    public void Cancel(){
        if (_voiceSilcer != null ){
            _voiceSilcer.Finish();
        }
    }

}
