
package com.example.develop.gapnotificationapp.rest.Pojo.Angry.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseAngry {

    @SerializedName("heartrate")
    @Expose
    public Heartrate heartrate;
    @SerializedName("emg")
    @Expose
    public Emg emg;
    @SerializedName("voice")
    @Expose
    public Voice voice;
    @SerializedName("face")
    @Expose
    public Face face;
    @SerializedName("angry-body")
    @Expose
    public Boolean angryBody;
    @SerializedName("angry-look")
    @Expose
    public Boolean angryLook;
    @SerializedName("angry-gap")
    @Expose
    public Boolean angryGap;
    @SerializedName("send-time")
    @Expose
    public String sendTime;

}
