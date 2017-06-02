package com.example.develop.gapnotificationapp.Ble;

/**
 * Created by ragro on 2017/04/03.
 */

public class BleContentManager {
    private BleContent _HeartRate;
    private BleContent _EMG;
    private BleContent _Mortor;
    public enum TYPE{
        HEART(0), EMG(1), MORTOR(2), NON(3);
        private String[] list = new String[]{"心拍", "筋電", "振動", "未登録"};
        private final int id;
        private TYPE(final int id){
            this.id = id;
        }
        public int getInt(){
            return this.id;
        }
        public String getString(){
            return list[this.id];
        }
    }


    // 心拍
    public void setHeartRate(BleContent ble){
        _HeartRate = ble;
    }
    public BleContent getHeartRate(){
        return _HeartRate;
    }
    // 筋電位
    public void setEMG(BleContent ble){
        _EMG = ble;
    }
    public BleContent getEMG(){
        return _EMG;
    }
    //振動モータ
    public void setMortor(BleContent ble) {
        _Mortor = ble;
    }
    public BleContent getMortor(){
        return _Mortor;
    }

    // 渡されたBleが心拍として登録されていたら0,筋電位だったら1,未登録なら-1
    public TYPE isRegistered(BleContent ble){
        if (_HeartRate != null && _HeartRate.getDevice().equals(ble.getDevice())){
            return TYPE.HEART;
        }
        if (_EMG != null && _EMG.getDevice().equals(ble.getDevice())){
            return TYPE.EMG;
        }
        if (_Mortor != null && _Mortor.getDevice().equals(ble.getDevice())) {
            return TYPE.MORTOR;
        }

        return TYPE.NON;
    }
    // 登録の解除
    public void Deregistration( BleContent ble){

        switch (isRegistered(ble)){
            case HEART:
                _HeartRate = null;
                break;
            case EMG:
                _EMG = null;
                break;
            case MORTOR:
                _Mortor = null;
                break;
        }
    }

}
