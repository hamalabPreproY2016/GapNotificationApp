package com.example.develop.gapnotificationapp.rest;


import com.example.develop.gapnotificationapp.rest.Pojo.Angry.response.ResponseAngry;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAverage.request.RequestAverage;
import com.example.develop.gapnotificationapp.rest.Pojo.EmgAverage.response.ResponseAverage;

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
    
    @POST("/emg-ave")
    Call<ResponseAverage> postEmgAverage(@Body RequestAverage body);

    @Multipart
    @POST("/angry")
    Call<ResponseAngry> postAngry(@Part MultipartBody.Part jsonData, @Part MultipartBody.Part voice, @Part MultipartBody.Part face);

}