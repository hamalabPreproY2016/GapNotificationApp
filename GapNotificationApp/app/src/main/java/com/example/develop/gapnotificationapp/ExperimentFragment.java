package com.example.develop.gapnotificationapp;


import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.develop.gapnotificationapp.camera.Camera;
import com.example.develop.gapnotificationapp.experiment.ExperimentManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExperimentFragment extends Fragment {

    @BindView(R.id.experiment_start_button)
    Button _startButton ;

    @BindView(R.id.camera_preview)
    TextureView _textureView;

    Camera camera;

    private ExperimentManager _expManager;
    public ExperimentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_experiment, container, false);
        ButterKnife.bind(this, view);

        _expManager = new ExperimentManager(getActivity());

        camera = new Camera(getContext(), _textureView);

        _textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
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

        GapNotificationApplication.getTakePictureRepeater(getContext()).setCamera(camera);

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.experiment_start_button)
    public void OnclickStart(){
        _expManager.Start();
    }

}
