package com.example.develop.gapnotificationapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by develop on 2017/03/29.
 */

public class BiometricManager {
    private List<Integer> RRIArray;

    BiometricManager() {
        RRIArray = new ArrayList<Integer>();
    }

    public void addRRI(int rri){
        RRIArray.add(rri);
    }
    public List<Integer> getRRIArray() { return  RRIArray; }
}
