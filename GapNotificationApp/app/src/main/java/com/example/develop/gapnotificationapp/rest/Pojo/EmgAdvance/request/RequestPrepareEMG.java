
package com.example.develop.gapnotificationapp.rest.Pojo.EmgAdvance.request;

import com.example.develop.gapnotificationapp.model.Emg;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestPrepareEMG {

    @SerializedName("emg")
    @Expose
    public List<Emg> emg = null;

}
