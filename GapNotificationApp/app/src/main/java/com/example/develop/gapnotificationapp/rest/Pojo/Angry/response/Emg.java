
package com.example.develop.gapnotificationapp.rest.Pojo.Angry.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Emg {

    @SerializedName("angryValue")
    @Expose
    public Double angryValue;
    @SerializedName("isMove")
    @Expose
    public Boolean isMove;

}
