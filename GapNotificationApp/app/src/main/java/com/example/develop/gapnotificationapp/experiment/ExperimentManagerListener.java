package com.example.develop.gapnotificationapp.experiment;

/**
 * Created by ragro on 2017/04/16.
 */

public abstract class ExperimentManagerListener {
    public abstract void GetHeartRate(SensorStruct.HeartRateStruct data);
    public abstract void GetEmg(SensorStruct.EmgStruct data);
    public abstract void GetVoice(SensorStruct.VoiceStruct data);
    public abstract void GetFace(SensorStruct.FaceStruct data);
}
