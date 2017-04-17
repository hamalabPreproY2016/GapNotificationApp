package com.example.develop.gapnotificationapp.model;

import java.io.File;

/**
 * Created by ragro on 2017/04/17.
 */

public class Voice extends PojoObject {
    public Long time;
    public File file;

    @Override
    public String[] toCSVStrings() {
        return new String[]{};
    }
}
