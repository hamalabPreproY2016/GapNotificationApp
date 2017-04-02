package com.example.develop.gapnotificationapp.Ble;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.example.develop.gapnotificationapp.GapNotificationApplication;
import com.example.develop.gapnotificationapp.R;
import com.example.develop.gapnotificationapp.util.HexString;
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

    private NotificationListener _listener;

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
    // 接続
    public void Connect(){
        connectionObservable
                .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(_writeUUID, this._writeBytes)
                        .flatMap(bytes ->rxBleConnection.setupNotification(_notifUUID))
                        .doOnNext(notificationObservable -> {
                            Log.d(TAG, "Notification has been set up");
                            // Notification has been set up
                        })
                        .flatMap(notificationObservable -> notificationObservable)

                )
                .subscribe(
                        bytes -> {
                            Log.d(TAG, "get notification data");
                            if (_listener != null) {
                                _listener.getNotification(bytes);
                            }
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );
    }
    // 接続解除
    public void DisConnect(){
        disconnectTriggerSubject.onNext(null);
    }
    // 接続中
    public boolean Connected(){
        return _bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    // 通知時のリスナーをセットする
    public void setNotificationListener(NotificationListener listener) {
        _listener = listener;
    }
}
