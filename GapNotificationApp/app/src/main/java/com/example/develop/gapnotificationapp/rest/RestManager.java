package com.example.develop.gapnotificationapp.rest;

import android.util.Log;

import com.example.develop.gapnotificationapp.rest.pojo.postHartPojo;
import com.example.develop.gapnotificationapp.rest.pojo.resultHartPojo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ragro on 2017/04/06.
 */

public class RestManager {
    private final String BASE_URL = "http://windows.hamalab.org/";
    private Retrofit _retrofit;
    private GapServer _service;

    public RestManager(){
        _retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _service = _retrofit.create(GapServer.class);
    }

    public Retrofit getRetrofit(){
        return _retrofit;
    }
    // serverに心拍をPOST
    public void postHartRate(postHartPojo body, Callback<resultHartPojo> listener){
        Call<resultHartPojo> call = _service.postHart(body);
        call.enqueue(listener);
    }

    // serverに筋電をPOST
//    public void postEMG(postEMGPojo body, Callback<resultEMGPojo> listener){
//        Call<resultHartPojo> call = _service.postHart(body);
//        call.enqueue(listener);
//    }

    // serverに音声をPOST
//    public void postVoice(postVoicePojo body, Callback<resultVoicePojo> listener){
//        Call<resultHartPojo> call = _service.postHart(body);
//        call.enqueue(listener);
//    }
    // serverに表情をPOST
//    public void postFace(postFacePojo body, Callback<resultFacePojo> listener){
//        Call<resultFacePojo> call = _service.postHart(body);
//        call.enqueue(listener);
//    }
}
