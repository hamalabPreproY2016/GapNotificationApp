package com.example.develop.gapnotificationapp.rest;


import com.example.develop.gapnotificationapp.model.ResponseAngry;
import com.example.develop.gapnotificationapp.rest.Pojo.CheckerPojo;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAdvance.request.RequestPrepareEMG;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAdvance.response.ResponseAverage;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAdvance.response.ResponseMVE;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ragro on 2017/04/06.
 */

public interface GapServer {
    
    @POST("/emg-ave")
    Call<ResponseAverage> postEmgAverage(@Body RequestPrepareEMG body);

    @POST("/emg-mve")
    Call<ResponseMVE> postEmgMVE(@Body RequestPrepareEMG body);

    @Multipart
    @POST("/angry")
    Call<ResponseAngry> postAngry(@Part MultipartBody.Part jsonData, @Part MultipartBody.Part voice, @Part MultipartBody.Part face);

    @GET("/checker")
    Call<CheckerPojo> checkServer();

}