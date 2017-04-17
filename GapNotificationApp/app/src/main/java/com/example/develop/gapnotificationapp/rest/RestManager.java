package com.example.develop.gapnotificationapp.rest;


import com.example.develop.gapnotificationapp.rest.Pojo.Angry.request.RequestAngry;
import com.example.develop.gapnotificationapp.rest.Pojo.Angry.response.ResponseAngry;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAverage.request.RequestAverage;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAverage.response.ResponseAverage;
import com.google.gson.Gson;

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
    public void postEmgAverage(RequestAverage body, Callback<ResponseAverage> listener){
        Call<ResponseAverage> call = _service.postEmgAverage(body);
        call.enqueue(listener);
    }
    // 取得したセンサーデータを送る
    public void postAngry(RequestAngry json, String voice_path, String face_path, Callback<ResponseAngry> listener){
        // 音声
        File voice_file = new File(voice_path);
        RequestBody voiceBody =
                RequestBody.create(
                        MediaType.parse("audio/wav; samplerate=44100"),
                        voice_file
                );
        MultipartBody.Part voice =
                MultipartBody.Part.createFormData("voice", voice_file.getName(), voiceBody);
        // 顔
        File face_file = new File(face_path);
        RequestBody faceBody =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        face_file
                );
        MultipartBody.Part face =
                MultipartBody.Part.createFormData("face", face_file.getName(), faceBody);

        // json
        Gson gson = new Gson();
        String jsonString = gson.toJson(json);
        MultipartBody.Part jsonData =
                MultipartBody.Part.createFormData("json", jsonString);
        // リスナーをセット
        Call<ResponseAngry> call = _service.postAngry(jsonData, voice, face);
        call.enqueue(listener);
    }


}