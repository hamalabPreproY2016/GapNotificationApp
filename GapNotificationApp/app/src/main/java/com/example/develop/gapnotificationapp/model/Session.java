package com.example.develop.gapnotificationapp.model;

import com.example.develop.gapnotificationapp.CSVManager;

/**
 * Created by ragro on 2017/05/15.
 */

public class Session extends PojoObject{
    public Long time;
    public Session(Long t){
        time = t;
    }
    @Override
    public String[] parseCSVLine(CSVManager manager) {
        return new String[]{
            time.toString()
        };
    }

    @Override
    public void setPropertyFromCSVLine(CSVManager manager, String[] strings) {
        time = new Long(Long.parseLong(strings[0]));
    }
}
