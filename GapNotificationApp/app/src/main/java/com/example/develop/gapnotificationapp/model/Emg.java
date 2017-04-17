
package com.example.develop.gapnotificationapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Emg {

    @SerializedName("time")
    @Expose
    public Integer time;
    @SerializedName("value")
    @Expose
    public Integer value;

}
