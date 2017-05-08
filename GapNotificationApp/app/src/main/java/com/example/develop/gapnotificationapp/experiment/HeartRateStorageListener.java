package com.example.develop.gapnotificationapp.experiment;

import com.example.develop.gapnotificationapp.model.Heartrate;

/**
 * Created by ragro on 2017/05/08.
 */

public abstract class HeartRateStorageListener {
    public abstract void Completed();
    public abstract void GetHeartRate(Heartrate heartrate);
}
