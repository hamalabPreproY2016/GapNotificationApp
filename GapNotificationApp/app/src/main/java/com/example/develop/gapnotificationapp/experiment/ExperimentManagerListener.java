package com.example.develop.gapnotificationapp.experiment;

import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.model.Face;
import com.example.develop.gapnotificationapp.model.Heartrate;
import com.example.develop.gapnotificationapp.model.Voice;

/**
 * Created by ragro on 2017/04/16.
 */

public abstract class ExperimentManagerListener {
    public abstract void GetHeartRate(Heartrate data);
    public abstract void GetEmg(Emg data);
    public abstract void GetVoice(Voice data);
    public abstract void GetFace(Face data);
}
