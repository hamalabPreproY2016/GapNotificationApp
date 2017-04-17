package com.example.develop.gapnotificationapp.experiment;

import com.example.develop.gapnotificationapp.CSVManager;

import java.io.File;

/**
 * Created by ragro on 2017/04/16.
 */

public class SensorStruct {
    public abstract class origin{
        public long time;

        public abstract String[] toCSVStrings();

        public origin(long _time) {
            time = _time;
        }

        public origin(String[] csvStrings) {

        }
    }

    public class VoiceStruct extends origin{
        public VoiceStruct(File _data, long _time){
            super(_time);
            data = _data;
        }

        public File data;

        public VoiceStruct(String[] csvStrings) {
            super(csvStrings);
            time = Integer.parseInt(csvStrings[0]);
//            data =
        }

        @Override
        public String[] toCSVStrings() {
            return new String[]{Long.toString(time), data.getName()};
        }
    }

    public CSVManager.ConvertFromStringsListener voiceConvertedListener = new CSVManager.ConvertFromStringsListener() {
        @Override
        public Object convertToObjectFromStrings(String[] strings) {
            return new VoiceStruct(strings);
        }
    };

    public class FaceStruct extends origin{
        public FaceStruct(File _data, long _time){
            super(_time);
            data = _data;
        }
        public File data;

        public FaceStruct(String[] csvStrings) {
            super(csvStrings);
        }

        @Override
        public String[] toCSVStrings() {
            return new String[]{Long.toString(time), data.getName()};
        }
    }

    public CSVManager.ConvertFromStringsListener faceConvertedListener = new CSVManager.ConvertFromStringsListener() {
        @Override
        public Object convertToObjectFromStrings(String[] strings) {
            return new FaceStruct(strings);
        }
    };

    public class HeartRateStruct  extends origin{
        public HeartRateStruct(Short _data, long _time){
            super(_time);
            data = _data;
        }
        public Short data;

        @Override
        public String[] toCSVStrings() {
            return new String[]{Long.toString(time), data.toString()};
        }
    }

    public class EmgStruct  extends origin{
        public EmgStruct(Short _data, long _time){
            super(_time);
            data = _data;
        }
        public Short data;

        @Override
        public String[] toCSVStrings() {
            return new String[]{Long.toString(time), data.toString()};
        }
    }
}
