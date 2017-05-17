package com.example.develop.gapnotificationapp.util;

import android.util.Log;

import static android.R.attr.src;

/**
 * Created by ragro on 2017/04/02.
 */

public class BinaryInteger {
    public static int TwoByteToInteger(byte[] bytes){
        if (1 <= bytes.length && bytes.length <= 2){
            short result;
            if(bytes.length == 1){
                result = (short) (bytes[0] << 2 & 0x3ff);
            } else{
                result = (short) ((bytes[1] << 2 & 0x3ff) | (bytes[0] << 10));
            }
            return result;
        }else {
            throw new IllegalArgumentException("Only 1 or 2 bytes");
        }
    }
    public static byte[] ShortToByte(Short data){
        byte[] bytes = new byte[2];
        bytes[0] = (byte)((data >> 8) & 0xff);
        bytes[1] = (byte)(data & 0xff);
        return bytes;
    }
}
