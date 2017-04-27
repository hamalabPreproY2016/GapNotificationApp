package com.example.develop.gapnotificationapp.model;

import com.example.develop.gapnotificationapp.CSVManager;
import com.example.develop.gapnotificationapp.util.FileHelper;

import java.io.File;

/**
 * Created by ragro on 2017/04/17.
 */

public class Voice extends PojoObject {
    public String time;
    public File file;

    @Override
    public String[] parseCSVLine(CSVManager manager) {
        String[] retStrings = new String[]{time, FileHelper.getRelativePath(file, manager.getCSVFile())};

        return retStrings;
    }

    @Override
    public void setPropertyFromCSVLine(CSVManager manager, String[] strings) {
        time = strings[0];
        file = FileHelper.getFileFromRelativePath(strings[1], manager.getCSVFile());
    }
}
