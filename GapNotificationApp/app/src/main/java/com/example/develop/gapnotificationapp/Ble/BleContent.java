package com.example.develop.gapnotificationapp.Ble;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.develop.gapnotificationapp.GapNotificationApplication;
import com.example.develop.gapnotificationapp.R;
import com.example.develop.gapnotificationapp.util.BinaryInteger;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.UUID;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by ragro on 2017/03/30.
 */

public class BleContent {

    public String TAG = "BLECONNTENT";

    private Context _context;

    private String _mac_address;
    private UUID _writeUUID;
    private UUID _notifUUID;

    private byte[] _writeBytes;
    private RxBleDevice _bleDevice;
    private PublishSubject<Void> disconnectTriggerSubject = PublishSubject.create();
    private Observable<RxBleConnection> connectionObservable;

    protected NotificationListener _listener;

    // コンストラクタ
    public BleContent(Context context, String mac_address, UUID WriteUUID, UUID NotifiUUID) {
        _mac_address = mac_address;
        _context = context;
        _writeUUID = WriteUUID;
        _notifUUID = NotifiUUID;
        _bleDevice = GapNotificationApplication.getRxBleClient(_context).getBleDevice(_mac_address);
        connectionObservable = prepareConnectionObservable();
        // byteデータの初期化
        byte[] tmp = new byte[1];
        tmp[0] = 0;
        _writeBytes = tmp;
        _listener = null;

        Log.d(TAG, "create BleContent");
    }
    public BleContent(){

    }

    public BleContent(Context context, String mac_address){
        this(context,
                mac_address,
                UUID.fromString(context.getResources().getString(R.string.uuid_write)),
                UUID.fromString(context.getResources().getString(R.string.uuid_notify)));
//        this(context,
//                mac_address,
//                UUID.fromString("569a2001-b87F-490c-92cb-11ba5ea5167c"),
//                UUID.fromString("569a2000-b87F-490c-92cb-11ba5ea5167c"));
    }
    // Observableの作成
    private Observable<RxBleConnection> prepareConnectionObservable() {
        return _bleDevice
                .establishConnection(_context, false)
                .takeUntil(disconnectTriggerSubject);
    }
    // 書込みデータをセット
    public void Write(byte[] WriteData){
        _writeBytes = WriteData;
        Connect();
    }
    public void ConnectRecive(){
        connectionObservable.subscribe(rxBleConnection -> {
            if (_listener != null) {
                _listener.Connected();
            }
        });
    }
    // 接続
    public void Connect(){
        Log.d(TAG, "きてる");
        connectionObservable
                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(_notifUUID))
                .doOnNext(notificationObservable -> {
                    // Notification has been set up
                })
                .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
                .subscribe(
                        bytes -> {
                                Log.d(TAG, "value : " + Integer.toString(BinaryInteger.TwoByteToInteger(bytes)));
                                _listener.getNotification(bytes);
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );
//                .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(_writeUUID, this._writeBytes)
//                        .flatMap(bytes ->rxBleConnection.setupNotification(_notifUUID))
//                        .doOnNext(notificationObservable -> {
//                            Log.d(TAG, "Notification has been set up");
//                            // Notification has been set up
//                        })
//                        .flatMap(notificationObservable -> notificationObservable)
//
//                )
//                .subscribe(
//                        bytes -> {
//                            Log.d(TAG, "get notification data");
//                            if (_listener != null) {
//                                Log.d(TAG, "value : " + Integer.toString(BinaryInteger.TwoByteToInteger(bytes)));
//                                _listener.getNotification(bytes);
//                            }
//                        },
//                        throwable -> {
//                            // Handle an error here.
//                        }
//                );
    }
    // 接続解除
    public void DisConnect(){
        disconnectTriggerSubject.onNext(null);
    }
    // 接続中
    public boolean Connected(){
        return _bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    public RxBleDevice getDevice(){
        return _bleDevice;
    }

    // 通知時のリスナーをセットする
    public void setNotificationListener(NotificationListener listener) {
        _listener = listener;
    }

}
