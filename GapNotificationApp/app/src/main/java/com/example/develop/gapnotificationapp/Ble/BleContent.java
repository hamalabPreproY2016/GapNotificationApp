package com.example.develop.gapnotificationapp.Ble;

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

abstract public class BleContent {
    private String _mac_address;
    private RxBleDevice _device;
    private Context _context;
    private UUID _writeUUID;
    private UUID _notifUUID;
    private byte[] _writeBytes;
    public String TAG = "BLECONNTENT";
    private PublishSubject<Void> disconnectTriggerSubject = PublishSubject.create();
    private RxBleDevice _bleDevice;
    private Observable<RxBleConnection> connectionObservable;

    // 更新されたデータを読み取り
    abstract public void Notification(byte[] readData);

    public BleContent(Context context, String mac_address, UUID WriteUUID, UUID NotifiUUID) {
        _mac_address = mac_address;
        _context = context;
        _writeUUID = WriteUUID;
        _notifUUID = NotifiUUID;
        _bleDevice = GapNotificationApplication.getRxBleClient(_context).getBleDevice(_mac_address);
        connectionObservable = prepareConnectionObservable();
        Log.d(TAG, "できてる");
    }
    // Observableの作成
    private Observable<RxBleConnection> prepareConnectionObservable() {
        return _bleDevice
                .establishConnection(_context, false)
                .takeUntil(disconnectTriggerSubject);
    }
    // 書込みデータ
    public void Write(byte[] WriteData){
        _writeBytes = WriteData;
    }

    // 接続
    public void Connect(){
        connectionObservable
                .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(_writeUUID, HexString.hexToBytes("00"))
                        .flatMap(bytes ->rxBleConnection.setupNotification(_notifUUID))
                        .doOnNext(notificationObservable -> {
                            Log.d(TAG, "Notification has been set up");
                            // Notification has been set up
                        })
                        .flatMap(notificationObservable -> notificationObservable)

                )
                .subscribe(
                        bytes -> {
                            Log.d(TAG, Integer.toString(bytes.length));
                            Notification(bytes);
                            // Written data.
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

}
