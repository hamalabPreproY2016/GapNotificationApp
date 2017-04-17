package com.example.develop.gapnotificationapp.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by develop on 2017/04/16.
 */

public class Camera {
    private Context mContext;
    private CameraDevice mCamera;
    private TextureView mTextureView;
    private Size mCameraSize;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private CameraManager manager;
    private SaveImageListener mListener = null;
    private File rootDirectory = null;

    private CameraDevice.StateCallback mCameraDeviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCamera = camera;
            createCaptureSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCamera = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCamera = null;
        }
    };

    CameraCaptureSession.StateCallback mCameraCaptureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mPreviewSession = session;
            updatePreview();
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.d("Camera", "failed");
        }
    };

    public Camera(Context context, TextureView textureView) {
        mContext = context;
        mTextureView = textureView;
    }

    public void open() {
        try {
            manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    mCameraSize = map.getOutputSizes(SurfaceTexture.class)[0];
                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        manager.openCamera(cameraId, mCameraDeviceCallback, null);

                        //カメラとTextureの向き補正
                        //http://qiita.com/cattaka/items/330321cb8c258c535e07
                        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        Matrix matrix = new Matrix();
                        int orientation = windowManager.getDefaultDisplay().getRotation();
                            matrix.postRotate(orientation * -90.0f, mTextureView.getWidth() * 0.5f, mTextureView.getHeight() * 0.5f);
//                            matrix.postScale(0.5f, 0.5f, mTextureView.getWidth() * 0.5f, mTextureView.getHeight() * 0.5f);

                            mTextureView.setTransform(matrix);
                    }
                    return;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCaptureSession() {
        if (!mTextureView.isAvailable()) {
            return;
        }

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(mCameraSize.getWidth(), mCameraSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            mPreviewBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        mPreviewBuilder.addTarget(surface);
        try {
            mCamera.createCaptureSession(Collections.singletonList(surface), mCameraCaptureSessionCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void setRootDirectory(File root) {
        rootDirectory = root;
    }

    public void setListener(SaveImageListener listener) {
        mListener = listener;
    }

    public void takePicture() {
        if(mCamera == null) {
            return;
        }

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCamera.getId());

            Size[] jpegSizes = null;

            int width = 640;
            int height = 480;

            if (characteristics != null) {
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                if (jpegSizes != null && 0 < jpegSizes.length) {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List outputSurfaces = new ArrayList(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // ファイルの保存先のディレクトリとファイル名.
//                String strSaveDir = Environment.getExternalStorageDirectory().toString();
            String strSaveDir = "image";
            String strSaveFileName = "pic_" + System.currentTimeMillis() +".jpg";

            if (rootDirectory == null) {
                return;
            }

            File imgDir = new File(rootDirectory, strSaveDir);
            if (!imgDir.exists() || !imgDir.isDirectory()) {
                imgDir.mkdir();
            }

            // 別スレッドで画像の保存処理を実行.
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();

                        // TODO: Fragmentで取得した画像を表示.保存ボタンが押されたら画像の保存を実行する.
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);

                        saveImage(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void saveImage(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
//                        output = mContext.openFileOutput(strSaveDir + "/" + strSaveFileName, Context.MODE_PRIVATE);
                        File imgFile = new File(imgDir, strSaveFileName);
                        output = new FileOutputStream(imgFile);
                        output.write(bytes);
                        if (mListener != null) {
                            mListener.OnSaveImageComplete(imgFile);
                        }
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };

            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroudHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroudHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request, TotalCaptureResult result) {
                    // 画像保存完了
                    super.onCaptureCompleted(session, request, result);
                    createCaptureSession();
                }
            };
            mCamera.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, backgroudHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, backgroudHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface SaveImageListener {
        abstract void OnSaveImageComplete(File file);
    }
}