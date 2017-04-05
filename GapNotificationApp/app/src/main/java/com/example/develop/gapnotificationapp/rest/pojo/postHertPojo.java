package com.example.develop.gapnotificationapp.rest.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class postHertPojo {

    @SerializedName("array")
    @Expose
    public List<Integer> array = null;

}