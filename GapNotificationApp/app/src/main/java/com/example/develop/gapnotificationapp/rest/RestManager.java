package com.example.develop.gapnotificationapp.rest;

import com.example.develop.gapnotificationapp.rest.pojo.postHeartPojo;
import com.example.develop.gapnotificationapp.rest.pojo.resultHeartPojo;
import com.example.develop.gapnotificationapp.rest.pojo.resultVoicePojo;
import com.example.develop.gapnotificationapp.voice.SoundDefine;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
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
    public void postHeartRate(postHeartPojo body, Callback<resultHeartPojo> listener){
        Call<resultHeartPojo> call = _service.postHeart(body);
        call.enqueue(listener);
    }

    // serverに筋電をPOST
//    public void postEMG(postEMGPojo body, Callback<resultEMGPojo> listener){
//        Call<resultHeartPojo> call = _service.postHart(body);
//        call.enqueue(listener);
//    }

    // serverに音声をPOST
    public void postVoiceFile(String file_name, Callback<resultVoicePojo> listener){
        File file = new File(file_name);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("audio/wav; samplerate=" + Integer.toString(SoundDefine.SAMPLING_RATE)),
                        file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("upload", file.getName(), requestFile);
        Call<resultVoicePojo> call = _service.postVoice(body);
        call.enqueue(listener);
    }

    // serverに表情をPOST
//    public void postFace(postFacePojo body, Callback<resultFacePojo> listener){
//        Call<resultFacePojo> call = _service.postHart(body);
//        call.enqueue(listener);
//    }
}
