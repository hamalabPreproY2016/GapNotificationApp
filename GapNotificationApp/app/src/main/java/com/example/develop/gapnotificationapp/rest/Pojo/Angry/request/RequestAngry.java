
package com.example.develop.gapnotificationapp.rest.Pojo.Angry.request;

import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.model.Heartrate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestAngry {

    @SerializedName("heartrate")
    @Expose
    public List<Heartrate> heartrate = null;
    @SerializedName("emg")
    @Expose
    public List<Emg> emg = null;
    @SerializedName("emg-mve")
    @Expose
    public Integer emgMve;
    @SerializedName("send-time")
    @Expose
    public String sendTime;

}
