package com.example.develop.gapnotificationapp.rest;

import com.example.develop.gapnotificationapp.rest.pojo.postHartPojo;
import com.example.develop.gapnotificationapp.rest.pojo.resultHartPojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ragro on 2017/04/06.
 */

public interface GapServer {
        @POST("/hart")
        Call<resultHartPojo> postHart(@Body postHartPojo body);
//        @POST("/voice")
//        Call<resultHartPojo> postVoice(String type, @Body postHartPojo body);
//        @POST("/face")
//        Call<resultHartPojo> postFace(String type, @Body postHartPojo body);
//        @POST("/emg")
//        Call<resultHartPojo> postEMG(String type, @Body postHartPojo body);
}
