package com.example.develop.gapnotificationapp;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.develop.gapnotificationapp.camera.Camera;
import com.example.develop.gapnotificationapp.experiment.ExperimentManager;
import com.example.develop.gapnotificationapp.experiment.ExperimentManagerListener;
import com.example.develop.gapnotificationapp.experiment.GetMveManager;
import com.example.develop.gapnotificationapp.experiment.GetMveManagerListener;
import com.example.develop.gapnotificationapp.experiment.HeartRateStorage;
import com.example.develop.gapnotificationapp.experiment.HeartRateStorageListener;
import com.example.develop.gapnotificationapp.model.Emg;
import com.example.develop.gapnotificationapp.model.Face;
import com.example.develop.gapnotificationapp.model.Heartrate;
import com.example.develop.gapnotificationapp.model.ResponseAngry;
import com.example.develop.gapnotificationapp.model.Voice;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExperimentFragment extends Fragment {

    @BindView(R.id.startHeartRateStorage)
    public Button getHeartRateToggle;

    @BindView(R.id.heartrate_status)
    public TextView heartRateStatus;
    @BindView(R.id.mve_toggle)
    public Button mveToggle;

    @BindView(R.id.mve_status_notification)
    public TextView mveStatus;

    @BindView(R.id.experiment_rriGraph)
    public LineChart rriGraph;

    @BindView(R.id.experiment_emgGraph)
    public LineChart emgGraph;

    @BindView(R.id.experiment_angryGraph)
    public HorizontalBarChart angryGraph;

    @BindView(R.id.experiment_start_button)
    Button _startButton ;

    boolean isRunning = false;

    @BindView(R.id.camera_preview)
    TextureView _textureView;

//    @BindView(R.id.tabhost)
//    TabHost _tabHost;

    public File csvDir = null;

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
        setEmgGraph();
        setAngryGraph();
        
        _startButton.setEnabled(false);

        previewCSVData();

        return view;
    }

    @OnClick(R.id.experiment_start_button)
    public void OnClick(){
        if (!isRunning ) {
            // MVEの測定が行われていなければ始めない
            if (_MVE == -1){
                return;
            }
            _expManager.Start(new ExperimentManagerListener() {
                @Override
                public void GetHeartRate(Heartrate heartrate) {
                    addPojoDataToHeartrateGraph(heartrate);

//                    LineData data = rriGraph.getLineData();
//
//                    ILineDataSet set = data.getDataSetByIndex(0);
//                    if (set == null) {
//                        set = new LineDataSet(null, "RRI");
//                        set.setDrawValues(false);
//                        data.addDataSet(set);
//                    }
//
//                    data.addEntry(new Entry(Float.parseFloat(heartrate.time), heartrate.value.intValue()), 0);
//
//                    rriGraph.notifyDataSetChanged();
//                    rriGraph.setVisibleXRangeMaximum(20000);
//                    rriGraph.setVisibleXRangeMinimum(20000);
//                    rriGraph.moveViewToX(Float.parseFloat(heartrate.time));
                }

                @Override
                public void GetEmg(Emg emg) {
                    addPojoDataToEmgGraph(emg);

//                    LineData data = emgGraph.getLineData();
//
//                    ILineDataSet set = data.getDataSetByIndex(0);
//                    if (set == null) {
//                        set = new LineDataSet(null, "RRI");
//                        set.setDrawValues(false);
//                        data.addDataSet(set);
//                    }
//
//                    data.addEntry(new Entry(Float.parseFloat(emg.time), emg.value.intValue()), 0);
//
//                    emgGraph.notifyDataSetChanged();
//                    emgGraph.setVisibleXRangeMaximum(20000);
//                    emgGraph.setVisibleXRangeMinimum(20000);
//                    emgGraph.moveViewToX(Float.parseFloat(emg.time));
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
                    addPojoDataToAngryGraph(response);

//                    float fSendTime = Float.parseFloat(response.sendTime);
//
//                    BarData data = angryGraph.getBarData();
//
//                    IBarDataSet set_body = data.getDataSetByIndex(0);
//                    BarDataSet newBody = addAngryDataToDataset(set_body, fSendTime, response.angryBody);
//
//                    IBarDataSet set_Look = data.getDataSetByIndex(1);
//                    BarDataSet newLook = addAngryDataToDataset(set_Look, fSendTime, response.angryLook);
//
//                    IBarDataSet set_Gap = data.getDataSetByIndex(2);
//                    BarDataSet newGap = addAngryDataToDataset(set_Gap, fSendTime, response.angryGap);
//
//                    BarData newData = new BarData();
//                    newData.addDataSet(newBody);
//                    newData.addDataSet(newLook);
//                    newData.addDataSet(newGap);
//
//                    angryGraph.setData(newData);
//
//                    angryGraph.notifyDataSetChanged();
//                    angryGraph.postInvalidate();
//
//                    YAxis axis = angryGraph.getAxisRight();
//                    axis.setAxisMaximum(Math.max(20000.0f, fSendTime));
//                    axis.setAxisMinimum(Math.max(0, fSendTime - 20000));
                }
            });
            _startButton.setText("すとっぷ");
        } else {
            _expManager.Finish();
            _startButton.setText("すたーと");
        }
        isRunning = !isRunning;
    }

    private void addPojoDataToHeartrateGraph(Heartrate heartrate) {
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

    private void addPojoDataToEmgGraph(Emg emg) {

        LineData data = emgGraph.getLineData();

        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = new LineDataSet(null, "RRI");
            set.setDrawValues(false);
            data.addDataSet(set);
        }

        data.addEntry(new Entry(Float.parseFloat(emg.time), emg.value.intValue()), 0);

        emgGraph.notifyDataSetChanged();
        emgGraph.setVisibleXRangeMaximum(20000);
        emgGraph.setVisibleXRangeMinimum(20000);
        emgGraph.moveViewToX(Float.parseFloat(emg.time));
    }

    private void addPojoDataToAngryGraph(ResponseAngry response) {

        float fSendTime = Float.parseFloat(response.sendTime);

        BarData data = angryGraph.getBarData();

        IBarDataSet set_body = data.getDataSetByIndex(0);
        BarDataSet newBody = addAngryDataToDataset(set_body, fSendTime, response.angryBody);

        IBarDataSet set_Look = data.getDataSetByIndex(1);
        BarDataSet newLook = addAngryDataToDataset(set_Look, fSendTime, response.angryLook);

        IBarDataSet set_Gap = data.getDataSetByIndex(2);
        BarDataSet newGap = addAngryDataToDataset(set_Gap, fSendTime, response.angryGap);

        BarData newData = new BarData();
        newData.addDataSet(newBody);
        newData.addDataSet(newLook);
        newData.addDataSet(newGap);

        angryGraph.setData(newData);

        angryGraph.notifyDataSetChanged();
        angryGraph.postInvalidate();

        YAxis axis = angryGraph.getAxisRight();
        axis.setAxisMaximum(Math.max(20000.0f, fSendTime));
        axis.setAxisMinimum(Math.max(0, fSendTime - 20000));
    }

    private BarDataSet addAngryDataToDataset(IBarDataSet dataSet, float time, boolean isAngry) {
        BarDataSet mDataSet = (BarDataSet)dataSet;
        BarEntry entry = dataSet.getEntryForIndex(0);
        float[] values = entry.getYVals();
        //最新の状態が怒りかどうか判定
        if (values.length % 2 != (isAngry ? 1 : 0)) {
            //違ったら要素を追加
            values = ArrayUtils.add(values, 0);
        }
        float intervalTime = time;

        for (int i = 0; i < values.length - 1; i++) {
            intervalTime -= values[i];
        }

        values[values.length - 1] = intervalTime;

        entry.setVals(values);

        float[] finalValues1 = values;
        BarDataSet retDataSet = new BarDataSet(new ArrayList<BarEntry>() {{
            add(new BarEntry((float)entry.getX(), finalValues1));
        }}, dataSet.getLabel());

        retDataSet.setColors(dataSet.getColors());
        retDataSet.setAxisDependency(dataSet.getAxisDependency());

        Log.d("angry", "val:" + values.toString());

        return retDataSet;
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void setEmgGraph() {
        emgGraph.setTouchEnabled(false);
        emgGraph.setDragEnabled(false);
        emgGraph.setScaleEnabled(false);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        emgGraph.setData(data);

        YAxis leftAxis = emgGraph.getAxisLeft();
        leftAxis.setAxisMaximum(1500);
        leftAxis.setAxisMinimum(0);

        emgGraph.getAxisRight().setEnabled(false);

        XAxis xAxis = emgGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void setAngryGraph() {
        angryGraph.setTouchEnabled(false);
        angryGraph.setDragEnabled(false);
        angryGraph.setScaleEnabled(false);

        YAxis bottomAxis = angryGraph.getAxisLeft();
        bottomAxis.setEnabled(false);

        XAxis xAxis = angryGraph.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] dataSetXLabels = new String[] {"body", "Look", "gap"};

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dataSetXLabels[(int)value - 1];
            }
        });

        BarData data = new BarData();
        data.setDrawValues(false);

        final int[] dataSetColors = new int[] {Color.CYAN, Color.MAGENTA};
        final String[] dataSetStackLabels = new String[] {"normal", "angry"};

        for (int i = 0; i < dataSetXLabels.length; i++) {
            int finalI = i;
            BarDataSet dataSet = new BarDataSet(new ArrayList<BarEntry>() {{
                add(new BarEntry((float)finalI + 1, new float[] {0}));
            }}, dataSetXLabels[i]);
            dataSet.setColors(dataSetColors);
            dataSet.setStackLabels(dataSetStackLabels);
            dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            data.addDataSet(dataSet);
        }
        angryGraph.setData(data);
    }

    private void previewCSVData() {
        if (csvDir == null || !csvDir.exists() || !csvDir.isDirectory()) {
            return;
        }

        rriGraph.setTouchEnabled(true);
        rriGraph.setDragEnabled(true);
        rriGraph.setScaleEnabled(true);

        CSVManager hCSVManager = new CSVManager(new File(csvDir, "heartrate.csv"));
        ArrayList<Heartrate> heartrates = (ArrayList<Heartrate>) hCSVManager.csvRead(new CSVManager.ParseObjectFactory() {
            @Override
            public CSVManager.CSVLineParser create() {
                return new Heartrate();
            }
        });


        heartrates.forEach(heartrate -> {
            addPojoDataToHeartrateGraph(heartrate);
        });

        CSVManager eCSVManager = new CSVManager(new File(csvDir, "heartrate.csv"));
        ArrayList<Emg> emgs = (ArrayList<Emg>) hCSVManager.csvRead(new CSVManager.ParseObjectFactory() {
            @Override
            public CSVManager.CSVLineParser create() {
                return new Emg();
            }
        });

        emgGraph.setTouchEnabled(true);
        emgGraph.setDragEnabled(true);
        emgGraph.setScaleEnabled(true);

        emgs.forEach(emg -> {
            addPojoDataToEmgGraph(emg);
        });

        CSVManager rCSVManager = new CSVManager(new File(csvDir, "responseAngry.csv"));
        ArrayList<ResponseAngry> responses = (ArrayList<ResponseAngry>) rCSVManager.csvRead(new CSVManager.ParseObjectFactory() {
            @Override
            public CSVManager.CSVLineParser create() {
                return new ResponseAngry();
            }
        });


    }

    @OnClick(R.id.mve_toggle)
    public void mveToggleClick(){
        // カウントダウン開始
        mveToggle.setEnabled(false);
        mveToggle.setText("MVE測定準備");
        // 表示用のカウントダウン(ほぼダミー)
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            int count = 5;
            @Override
            public void run() {
                // UIスレッド
                count--;
                if (count < 0) {
                    // mve測定を開始
                    startGetMve();
                    return;
                }
                mveStatus.setText("測定まで " + Integer.toString(count));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(r);
    }
    private int _MVE = -1;
    // mve測定を開始
    public void startGetMve(){
        mveToggle.setText("MVE測定中");
        GetMveManager manager = new GetMveManager();
        manager.setListener(new GetMveManagerListener() {
            @Override
            public void getMve(int mve) {
                // 測定終了し実験開始可能へ
                _MVE = mve;
                mveToggle.setText("MVE測定開始");
                mveToggle.setEnabled(true);
                mveStatus.setText("MVE : " + Integer.toString(_MVE));
                _expManager.SetMve(mve);
                _startButton.setEnabled(_expManager.CanStart());
            }
        });
        // 表示用のカウントダウン(ほぼダミー)
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            int count = 10;
            @Override
            public void run() {
                // UIスレッド
                count--;
                if (count < 0) { // 5回実行したら終了
                    return;
                }
                mveStatus.setText("測定中" + Integer.toString(count));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(r);
        manager.Start();
    }
    HeartRateStorage hrStorage;

    @OnClick(R.id.startHeartRateStorage)
    public void heartrateToggleClick(){
        getHeartRateToggle.setEnabled(false);
        hrStorage = new HeartRateStorage(getContext());
        hrStorage.SetHeartRateListener(new HeartRateStorageListener() {
            @Override
            public void Completed() {
                getHeartRateToggle.setEnabled(true);
                heartRateStatus.setText("測定完了 : " + Integer.toString(hrStorage.GetSize()));
                _expManager.SetHeartRate(hrStorage.GetHeartRate());
                _startButton.setEnabled(_expManager.CanStart());
            }

            @Override
            public void GetHeartRate(Heartrate heartrate) {
                // 測定中
                heartRateStatus.setText("測定中 残り : " + Integer.toString(hrStorage.GetMaxSize() - hrStorage.GetSize()));
                Log.d("experiment", Integer.toString(hrStorage.GetSize()));

            }
        });
        hrStorage.Start();
    }
}
