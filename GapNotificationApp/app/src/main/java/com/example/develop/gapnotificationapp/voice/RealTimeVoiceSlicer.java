package com.example.develop.gapnotificationapp.voice;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ragro on 2017/04/09.
 */

public class RealTimeVoiceSlicer {

    public static final String TAG = "RecordProto";
    private String _directory;
    private int _counter;
    private Context _context;
    private int _interval_milli_second = 5000;
    private Timer timer ;
    private  VoiceSliceListener _listener = null;
    private String _current_file_name;

    // コンストラクタ
    public RealTimeVoiceSlicer(Context context){
        _context = context;
    }
    // スライサーの開始
    public void Start(String directory_name){
        _directory = directory_name;
        _counter = 0;

        // 5秒間隔でスライスを開始する
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                next();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, _interval_milli_second);
    }
    // 停止 (一度停止したらもう戻せないよ
    public void Stop(){
        timer.cancel();
    }
    // 次の録音を始める
    private void next(){
        //  最初の録音時ではないとき
        if (audioRecord != null ) {
            // 前のレコードを停止
            stopAudioRecord();
            // リスナーが設定されていれば呼び出す
            if (_listener != null) {
                _listener.Recorded(new File(_current_file_name));
            }
        }
        // 新しいレコードファイル名を取得
        _current_file_name = new File(_directory ,String.format("%03d.wav", _counter)).toString();
        // 新しいレコードファイルを作成
        initAudioRecord(_current_file_name);
        // レコードを開始
        startAudioRecord();
        Log.d("RecordProto", _current_file_name);
        _counter ++;
    }
    // リスナーをセット
    public void setVoiceSliceListener(VoiceSliceListener listener){
        _listener = listener;
    }

    // 音声関係のプロパティ
    AudioRecord audioRecord; //録音用のオーディオレコードクラス
    private short[] shortData; //オーディオレコード用バッファ
    private WaveFile nowRecordingWavFile = new WaveFile();
    private static final int SAMPLING_RATE = SoundDefine.SAMPLING_RATE;
    private int bufSize;//オーディオレコード用バッファのサイズ

    // 新しいレコードファイルを作成
    private void initAudioRecord(String file_name){
        nowRecordingWavFile.createFile(file_name);
        // AudioRecordオブジェクトを作成
        bufSize = AudioRecord.getMinBufferSize(SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufSize);

        shortData = new short[bufSize / 2];

        // コールバックを指定
        audioRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
            // フレームごとの処理
            @Override
            public void onPeriodicNotification(AudioRecord recorder) {
                audioRecord.read(shortData, 0, bufSize / 2); // 読み込む
                nowRecordingWavFile.addBigEndianData(shortData); // ファイルに書き出す
            }

            @Override
            public void onMarkerReached(AudioRecord recorder) {
                // TODO Auto-generated method stub

            }
        });
        // コールバックが呼ばれる間隔を指定
        audioRecord.setPositionNotificationPeriod(bufSize / 2);
    }

    // オーディオレコードを開始する
    private void startAudioRecord(){
        audioRecord.startRecording();
        audioRecord.read(shortData, 0, bufSize/2);
    }

    //オーディオレコードを停止する
    private void stopAudioRecord(){
            audioRecord.stop();
    }
}
