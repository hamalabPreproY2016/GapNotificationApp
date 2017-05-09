
package com.example.develop.gapnotificationapp.model;

import com.example.develop.gapnotificationapp.CSVManager;
import com.example.develop.gapnotificationapp.rest.Pojo.Angry.response.Emg;
import com.example.develop.gapnotificationapp.rest.Pojo.Angry.response.Face;
import com.example.develop.gapnotificationapp.rest.Pojo.Angry.response.Voice;
import com.example.develop.gapnotificationapp.rest.Pojo.Angry.response.Heartrate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseAngry extends PojoObject {

    @SerializedName("heartrate")
    @Expose
    public Heartrate heartrate;
    @SerializedName("emg")
    @Expose
    public Emg emg;
    @SerializedName("voice")
    @Expose
    public Voice voice;
    @SerializedName("face")
    @Expose
    public Face face;
    @SerializedName("angry-body")
    @Expose
    public Boolean angryBody;
    @SerializedName("angry-look")
    @Expose
    public Boolean angryLook;
    @SerializedName("angry-gap")
    @Expose
    public Boolean angryGap;
    @SerializedName("send-time")
    @Expose
    public String sendTime;


    @Override
    public String[] parseCSVLine(CSVManager manager) {
        return new String[]{
                sendTime,
                heartrate.angryValue.toString(),
                emg.angryValue.toString(),
                voice.angryValue.toString(),
                face.angryValue.toString(),
                angryBody.toString(),
                angryLook.toString(),
                angryGap.toString()
        };
    }

    @Override
    public void setPropertyFromCSVLine(CSVManager manager, String[] strings) {
        heartrate = new Heartrate();
        emg = new Emg();
        voice = new Voice();
        face = new Face();

        sendTime = strings[0];
        heartrate.angryValue = Double.parseDouble(strings[1]);
        emg.angryValue = Double.parseDouble(strings[2]);
        voice.angryValue = Double.parseDouble(strings[3]);
        face.angryValue = Double.parseDouble(strings[4]);

        angryBody = Boolean.parseBoolean(strings[5]);
        angryLook = Boolean.parseBoolean(strings[6]);
        angryGap = Boolean.parseBoolean(strings[7]);
    }
}
