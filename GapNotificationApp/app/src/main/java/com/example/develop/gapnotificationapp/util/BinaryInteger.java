package com.example.develop.gapnotificationapp.util;

/**
 * Created by ragro on 2017/04/02.
 */

public class BinaryInteger {
    public static int TwoByteToInteger(byte[] bytes){
        if (1 <= bytes.length && bytes.length <= 2){
            short result;
            if(bytes.length == 1){
                result = (short) (bytes[0] & 0xff);
            } else{
                result = (short) ((bytes[1] & 0xff) | (bytes[0] << 8));
            }
            return result;
        }else {
            throw new IllegalArgumentException("Only 1 or 2 bytes");
        }
    }
}
