package com.example.develop.gapnotificationapp.camera;

import android.os.Handler;

import java.io.File;

/**
 * Created by develop on 2017/04/17.
 */

public class TakePictureRepeater {
    private Camera mCamera;
    private Handler handler = null;
    private Runnable runnable = null;
    final int interval = 5000;

    //5秒おきに画像を取得
    public void startCapturePicture(File root, Camera.SaveImageListener listener) {
        if (mCamera == null || handler != null) {
            return;
        }

        mCamera.setRootDirectory(root);
        mCamera.setListener(listener);

        //5秒おきに実行
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(mCamera == null) {
                    return;
                }
                mCamera.takePicture();
                handler.postDelayed(this, interval);
            }
        };

        handler.postDelayed(runnable, interval);
    }

    //取得終了
    public void invalidateCapturePicture() {
        if (handler == null) {
            return;
        }
        handler.removeCallbacks(runnable);
        runnable = null;
        handler = null;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }
}
