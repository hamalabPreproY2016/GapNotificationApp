package com.example.develop.gapnotificationapp;

import android.content.Context;

import com.example.develop.gapnotificationapp.rest.RestManager;
import com.example.develop.gapnotificationapp.rest.pojo.postHeartPojo;
import com.example.develop.gapnotificationapp.rest.pojo.resultHeartPojo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by develop on 2017/03/29.
 */

public class BiometricManager {
    private List<Integer> RRIArray;

    private UpdateLFHFListener listener;
    private Context context;
    private RestManager restManager;

    BiometricManager(Context _context, UpdateLFHFListener _listener) {
        context = _context;
        listener = _listener;

        RRIArray = new ArrayList<Integer>();
        restManager = GapNotificationApplication.getRestManager(context);
    }

    public void addRRI(int rri){
        RRIArray.add(rri);

        if (RRIArray.size() >= 256) {
            postHeartPojo body = new postHeartPojo();
            body.array = RRIArray.subList(RRIArray.size() - 256, RRIArray.size());
            restManager.postHartRate(body, new Callback<resultHeartPojo>() {
                @Override
                public void onResponse(Call<resultHeartPojo> call, Response<resultHeartPojo> response) {
                    if (listener != null)
                        listener.updateLFHF(response.body().result);
                }

                @Override
                public void onFailure(Call<resultHeartPojo> call, Throwable t) {

                }
            });
        }
    }

    public List<Integer> getRRIArray() {
        return  RRIArray;
    }
}
