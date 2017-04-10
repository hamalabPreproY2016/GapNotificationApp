package com.example.develop.gapnotificationapp.rest;

import com.example.develop.gapnotificationapp.rest.pojo.postHeartPojo;
import com.example.develop.gapnotificationapp.rest.pojo.resultHeartPojo;
import com.example.develop.gapnotificationapp.rest.pojo.resultVoicePojo;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ragro on 2017/04/06.
 */

public interface GapServer {
        // 心拍取得
        @POST("/heart")
        Call<resultHeartPojo> postHeart(@Body postHeartPojo body);

        // 音声取得
        @Multipart
        @POST("/voice")
        Call<resultVoicePojo> postVoice(@Part MultipartBody.Part file);

//        @POST("/face")
//        Call<resultHeartPojo> postFace(String type, @Body postHeartPojo body);
//        @POST("/emg")
//        Call<resultHeartPojo> postEMG(String type, @Body postHeartPojo body);
}
