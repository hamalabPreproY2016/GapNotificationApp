package com.example.develop.gapnotificationapp.voice;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

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
    // 停止
    public void Stop(){
        timer.cancel();
    }
    // 次の録音を始める
    private void next(){
        String file_name = _directory + "/" + String.format("%03d.wav", _counter) ;
        // 前のレコードを停止
        stopAudioRecord();
        // 新しいレコードファイルを作成
        initAudioRecord(file_name);
        // レコードを開始
        startAudioRecord();
        Log.d("RecordProto", file_name);
        _counter ++;
    }

    // 音声関係のfile
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
        if (audioRecord != null ){
            audioRecord.stop();
        }
    }
}
