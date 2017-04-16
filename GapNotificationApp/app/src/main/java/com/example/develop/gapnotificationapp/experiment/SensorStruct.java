package com.example.develop.gapnotificationapp.experiment;

import java.io.File;

/**
 * Created by ragro on 2017/04/16.
 */

public class SensorStruct {
    public class origin{
        public long time;
    }

    public class VoiceStruct extends origin{
        public VoiceStruct(File _data, long _time){
            data = _data;
            time = _time;
        }
        public File data;
        public Double isAngry;
    }

    public class FaceStruct  extends origin{
        public FaceStruct(File _data, long _time){
            data = _data;
            time = _time;
        }
        public File data;
        public boolean isAngry;
    }

    public class HeartRateStruct  extends origin{
        public HeartRateStruct(Short _data, long _time){
            data = _data;
            time = _time;
        }
        public Short data;
        public boolean isAngry;
    }

    public class EmgStruct  extends origin{
        public EmgStruct(Short _data, long _time){
            data = _data;
            time = _time;
        }
        public Short data;
        public boolean isAngry;
    }

}
