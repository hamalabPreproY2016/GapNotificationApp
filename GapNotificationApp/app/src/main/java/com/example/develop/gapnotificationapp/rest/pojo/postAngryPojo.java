package com.example.develop.gapnotificationapp.rest.pojo;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ragro on 2017/04/12.
 */

public class postAngryPojo {
    @SerializedName("voice")
    @Expose
    public List<Double> voice = null;
    @SerializedName("emotion")
    @Expose
    public List<Boolean> emotion = null;
    @SerializedName("heart_rate")
    @Expose
    public List<Boolean> heartRate = null;
    @SerializedName("emg")
    @Expose
    public List<Boolean> emg = null;
}
