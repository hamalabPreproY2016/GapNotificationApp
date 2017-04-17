
package com.example.develop.gapnotificationapp.rest.Pojo.Angry.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Heartrate {

    @SerializedName("time")
    @Expose
    public Integer time;
    @SerializedName("value")
    @Expose
    public Integer value;

}
