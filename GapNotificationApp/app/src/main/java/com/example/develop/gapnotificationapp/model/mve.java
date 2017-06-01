
package com.example.develop.gapnotificationapp.model;

import com.example.develop.gapnotificationapp.CSVManager;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class mve extends PojoObject{

    public String time;
    public Integer value;

    @Override
    public String[] parseCSVLine(CSVManager manager) {
        return new String[] {time, value.toString()};
    }

    @Override
    public void setPropertyFromCSVLine(CSVManager manager, String[] strings) {
        time = strings[0];
        value = Integer.parseInt(strings[1]);
    }
}
