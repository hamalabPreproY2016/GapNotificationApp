package com.example.develop.gapnotificationapp;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.develop.gapnotificationapp.camera.Camera;
import com.example.develop.gapnotificationapp.experiment.ExperimentManager;
import com.example.develop.gapnotificationapp.experiment.ExperimentManagerListener;
import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.model.Face;
import com.example.develop.gapnotificationapp.model.Heartrate;
import com.example.develop.gapnotificationapp.model.ResponseAngry;
import com.example.develop.gapnotificationapp.model.Voice;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExperimentFragment extends Fragment {

    @BindView(R.id.experiment_rriGraph)
    public LineChart rriGraph;

    @BindView(R.id.experiment_angryGraph)
    public LineChart angryGraph;

    @BindView(R.id.experiment_start_button)
    Button _startButton ;

    boolean isRunning = false;

    @BindView(R.id.camera_preview)
    TextureView _textureView;

//    @BindView(R.id.tabhost)
//    TabHost _tabHost;

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

//        _tabHost.setup();
//
//        TabHost.TabSpec tab1 = _tabHost.newTabSpec("tab1");
//        tab1.setIndicator("カメラ");
//        tab1.setContent(R.id.tab1);
//        _tabHost.addTab(tab1);
//
//        TabHost.TabSpec tab2 = _tabHost.newTabSpec("tab2");
//        tab2.setIndicator("グラフ");
//        tab2.setContent(R.id.tab2);
//        _tabHost.addTab(tab2);
//
//        _tabHost.setCurrentTab(0);

        // Inflate the layout for this fragment

        setRRIGraph();
        setAngryGraph();

        return view;
    }

    @OnClick(R.id.experiment_start_button)
    public void OnClick(){
        if (!isRunning) {
            _expManager.Start(new ExperimentManagerListener() {
                @Override
                public void GetHeartRate(Heartrate heartrate) {
                    LineData data = rriGraph.getLineData();

                    ILineDataSet set = data.getDataSetByIndex(0);
                    if (set == null) {
                        set = new LineDataSet(null, "RRI");
                        set.setDrawValues(false);
                        data.addDataSet(set);
                    }

                    data.addEntry(new Entry(Float.parseFloat(heartrate.time), heartrate.value.intValue()), 0);

                    rriGraph.notifyDataSetChanged();
                    rriGraph.setVisibleXRangeMaximum(20000);
                    rriGraph.setVisibleXRangeMinimum(20000);
                    rriGraph.moveViewToX(Float.parseFloat(heartrate.time));
                }

                @Override
                public void GetEmg(Emg data) {

                }

                @Override
                public void GetVoice(Voice data) {

                }

                @Override
                public void GetFace(Face data) {

                }

                @Override
                public void GetEmgAverage(int average) {

                }

                @Override
                public void GetAngry(ResponseAngry response) {
                    LineData data = angryGraph.getLineData();

                    ILineDataSet set_body = data.getDataSetByIndex(0);
                    if (set_body == null) {
                        LineDataSet lineData = new LineDataSet(null, "AngryBody");
                        lineData.setColor(Color.RED);
                        lineData.setDrawFilled(true);
                        lineData.setDrawValues(false);
                        data.addDataSet(lineData);
                    }
                    data.addEntry(new Entry(Float.parseFloat(response.sendTime), response.angryBody ? 1 : 0), 0);

                    ILineDataSet set_Look = data.getDataSetByIndex(1);
                    if (set_Look == null) {
                        LineDataSet lineData = new LineDataSet(null, "AngryLook");
                        lineData.setColor(Color.BLUE);
                        lineData.setDrawFilled(true);
                        lineData.setDrawValues(false);
                        data.addDataSet(lineData);
                    }
                    data.addEntry(new Entry(Float.parseFloat(response.sendTime), response.angryLook ? 1 : 0), 1);

                    ILineDataSet set_Gap = data.getDataSetByIndex(2);
                    if (set_Gap == null) {
                        LineDataSet lineData = new LineDataSet(null, "AngryGap");
                        lineData.setColor(Color.YELLOW);
                        lineData.setDrawFilled(true);
                        lineData.setDrawValues(false);
                        data.addDataSet(lineData);
                    }
                    data.addEntry(new Entry(Float.parseFloat(response.sendTime), response.angryGap ? 1 : 0), 2);

                    angryGraph.notifyDataSetChanged();
                    angryGraph.setVisibleXRangeMaximum(20000);
                    angryGraph.setVisibleXRangeMinimum(20000);
                    angryGraph.moveViewToX(Float.parseFloat(response.sendTime));
                }
            });
            _startButton.setText("すとっぷ");
        } else {
            _expManager.Finish();
            _startButton.setText("すたーと");
        }
        isRunning = !isRunning;
    }

    private void setRRIGraph() {
        rriGraph.setTouchEnabled(false);
        rriGraph.setDragEnabled(false);
        rriGraph.setScaleEnabled(false);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        rriGraph.setData(data);

        YAxis leftAxis = rriGraph.getAxisLeft();
        leftAxis.setAxisMaximum(1500);
        leftAxis.setAxisMinimum(0);

        rriGraph.getAxisRight().setEnabled(false);

        XAxis xAxis = rriGraph.getXAxis();
    }

    private void setAngryGraph() {
        angryGraph.setTouchEnabled(false);
        angryGraph.setDragEnabled(false);
        angryGraph.setScaleEnabled(false);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        angryGraph.setData(data);

        YAxis leftAxis = angryGraph.getAxisLeft();
        leftAxis.setAxisMaximum(1);
        leftAxis.setAxisMinimum(0);

        angryGraph.getAxisRight().setEnabled(false);

        XAxis xAxis = angryGraph.getXAxis();
    }
}
