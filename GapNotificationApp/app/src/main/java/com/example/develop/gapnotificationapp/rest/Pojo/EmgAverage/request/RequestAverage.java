
package com.example.develop.gapnotificationapp.rest.Pojo.EmgAverage.request;

import com.example.develop.gapnotificationapp.rest.Pojo.Emg;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestAverage {

    @SerializedName("emg")
    @Expose
    public List<Emg> emg = null;

}
