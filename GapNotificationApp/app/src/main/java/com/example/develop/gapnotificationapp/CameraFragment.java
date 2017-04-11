package com.example.develop.gapnotificationapp;


import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    View view;

    Camera camera;

    @BindView(R.id.textureView)
    TextureView textureView;

    @BindView(R.id.imageView2)
    ImageView imageView;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_camera, container, false);

        ButterKnife.bind(this, view);

        camera = new Camera(new TextureView(getContext()));

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                camera.open();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

        return view;
    }

    @OnClick(R.id.button_takePicture)
    public void pushTakePictureButton() {
        camera.takePicture();
    }

    class Camera {
        private CameraDevice mCamera;
        private TextureView mTextureView;
        private Size mCameraSize;
        private CaptureRequest.Builder mPreviewBuilder;
        private CameraCaptureSession mPreviewSession;
        private CameraManager manager;

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

        public Camera(TextureView textureView) {
            mTextureView = textureView;
        }

        public void open() {
            try {
                manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        mCameraSize = map.getOutputSizes(SurfaceTexture.class)[0];
                        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            manager.openCamera(cameraId, mCameraDeviceCallback, null);

                            //カメラとTextureの向き補正
                            //http://qiita.com/cattaka/items/330321cb8c258c535e07
                            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                            Matrix matrix = new Matrix();
                            int orientation = windowManager.getDefaultDisplay().getRotation();
//                            matrix.postRotate(orientation * -90.0f, mTextureView.getWidth() * 0.5f, mTextureView.getHeight() * 0.5f);
//                            matrix.postScale(0.5f, 0.5f, mTextureView.getWidth() * 0.5f, mTextureView.getHeight() * 0.5f);

//                            mTextureView.setTransform(matrix);
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
                outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

                final CaptureRequest.Builder captureBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                // ファイルの保存先のディレクトリとファイル名.
//                String strSaveDir = Environment.getExternalStorageDirectory().toString();
                String strSaveDir = "tmpImg";
                String strSaveFileName = "pic_" + System.currentTimeMillis() +".jpg";

                Log.d("saveFile", strSaveDir + strSaveFileName);

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
                            output = getContext().openFileOutput(strSaveFileName, Context.MODE_PRIVATE);
                            output.write(bytes);
                        } finally {
                            if (null != output) {
                                output.close();

                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            File savefile = new File(getContext().getFilesDir(), strSaveFileName);
                                            Log.d("filesize", "" + savefile.length());

                                            InputStream is = new FileInputStream(savefile);
                                            Bitmap bm = BitmapFactory.decodeStream(is);
                                            imageView.setImageBitmap(bm);

                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
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


//                        File[] files = new File(strSaveDir).listFiles();
//                        for (File f: files) {
////                            Log.d("file", f.getName());
//                        }
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
                // 保存した画像を反映させる.
                String[] paths = {strSaveFileName};
                String[] mimeTypes = {"image/jpeg"};
                MediaScannerConnection.scanFile(getActivity().getApplicationContext(), paths, mimeTypes, mScanSavedFileCompleted);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        private MediaScannerConnection.OnScanCompletedListener mScanSavedFileCompleted = new MediaScannerConnection.OnScanCompletedListener(){
            @Override
            public void onScanCompleted(String path,
                                        Uri uri){
                // このタイミングでToastを表示する?
            }
        };
    }
}
