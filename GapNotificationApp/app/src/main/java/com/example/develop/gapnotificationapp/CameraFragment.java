package com.example.develop.gapnotificationapp;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.develop.gapnotificationapp.camera.Camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

        camera = new Camera(getContext(), textureView);
        camera.setListener(new Camera.SaveImageListener() {
                               @Override
                               public void OnSaveImageComplete(File file) {
                                   Handler mainHandler = new Handler(Looper.getMainLooper());

                                   mainHandler.post(new Runnable() {
                                       @Override
                                       public void run() {
                                           try {
                                               InputStream stream = new FileInputStream(file);
                                               Bitmap bm = BitmapFactory.decodeStream(stream);
                                               imageView.setImageBitmap(bm);
                                           } catch (FileNotFoundException e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   });
                               }
                           }
        );

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
}
