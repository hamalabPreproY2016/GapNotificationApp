
package com.example.develop.gapnotificationapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Heartrate {

    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("value")
    @Expose
    public Integer value;

}
