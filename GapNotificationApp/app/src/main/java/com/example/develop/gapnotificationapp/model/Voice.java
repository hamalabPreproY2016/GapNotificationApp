package com.example.develop.gapnotificationapp.model;

import com.example.develop.gapnotificationapp.CSVManager;

import java.io.File;

/**
 * Created by ragro on 2017/04/17.
 */

public class Voice extends PojoObject {
    public String time;
    public File file;


    @Override
    public String[] parseCSVLine(CSVManager manager) {
        return new String[]{time, file.getName()};
    }

    @Override
    public void setPropertyFromCSVLine(CSVManager manager, String[] strings) {

    }
}
