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
    private MediaRecorder recorder = null;

    // コンストラクタ
    public RealTimeVoiceSlicer(Context context){
        _context = context;
    }
    // スライサーの開始
    public void Start(String directory_name){
        // 指定ディレクトリの中にvoiceディレクトリを作成
        File root = new File(directory_name, "voice");
        root.mkdir();
        _directory = root.toString();
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
    public void Finish(){
        timer.cancel();
        startAudioRecord();
    }
    // 次の録音を始める
    private void next(){

        //  既に録音していたら停止する
        if (recorder != null ) {
            // 前のレコードを停止
            stopAudioRecord();
            // リスナーが設定されていれば呼び出す
            if (_listener != null) {
                _listener.Recorded(new File(_current_file_name));
            }
        }
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        // 新しいレコードファイル名を取得
        _current_file_name = new File(_directory ,String.format("%03d.wav", _counter)).toString();
        recorder.setOutputFile(_current_file_name);

        // レコードを開始
        startAudioRecord();

        Log.d("RecordProto", _current_file_name);
        _counter ++;
    }
    // リスナーをセット
    public void setVoiceSliceListener(VoiceSliceListener listener){
        _listener = listener;
    }

    // オーディオレコードを開始する
    private void startAudioRecord(){
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //オーディオレコードを停止する
    private void stopAudioRecord(){
        recorder.stop();
    }
}
