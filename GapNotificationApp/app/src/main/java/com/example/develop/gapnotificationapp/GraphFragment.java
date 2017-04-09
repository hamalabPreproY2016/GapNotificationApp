package com.example.develop.gapnotificationapp;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.develop.gapnotificationapp.Ble.BleContent;
import com.example.develop.gapnotificationapp.Ble.NotificationListener;
import com.example.develop.gapnotificationapp.util.BinaryInteger;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {
    View view;
    BiometricManager biometricManager;

    @BindView(R.id.rriGraph)
    public LineChart rriGraph;

    @BindView(R.id.graph_fragment_container)
    public LinearLayout layout;

    @BindView(R.id.rriCount)
    public TextView rriCountTextView;

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_graph, container, false);

        ButterKnife.bind(this, view);
        Log.d("graphFragment", "onCreateView");
        setRRIGraph();

        return view;
    }

    @OnClick(R.id.button_vibration)
    public void pushSendButton() {
        BleContent heartRateContent = GapNotificationApplication.getBleContentManager(getActivity()).getHeartRate();
        byte[] bytes = new byte[1];
        bytes[0] = 1;
        heartRateContent.Write(bytes);
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

        layout.setBackgroundColor(Color.WHITE);

        biometricManager = new BiometricManager(getActivity(), new UpdateLFHFListener() {
            @Override
            public void updateLFHF(boolean isAngry) {
                Log.d("LFHFResult", Boolean.toString(isAngry));
                if (isAngry) {
                    layout.setBackgroundColor(Color.GRAY);
                } else {
                    layout.setBackgroundColor(Color.WHITE);
                }
            }
        });

        Log.d("graphFragment", "setrrigraph");

        BleContent heartRateContent = GapNotificationApplication.getBleContentManager(getActivity()).getHeartRate();
        heartRateContent.setNotificationListener(new NotificationListener() {
            @Override
            public void getNotification(byte[] bytes) {
                int num = BinaryInteger.TwoByteToInteger(bytes);

                biometricManager.addRRI(num);
//                        rriCountTextView.setText("rri:" + Integer.toString(num) + " count:" + Integer.toString(biometricManager.getRRIArray().size()));

                ILineDataSet set = data.getDataSetByIndex(0);
                if (set == null) {
                    set = new LineDataSet(null, "RRI");
                    set.setDrawValues(false);
                    data.addDataSet(set);
                }

                List<Integer> rriArray = biometricManager.getRRIArray();

                int passtime = rriArray.stream().reduce(0, (base, value) -> base + value);

                data.addEntry(new Entry(passtime, num), 0);

                rriGraph.notifyDataSetChanged();
                rriGraph.setVisibleXRangeMaximum(20000);
                rriGraph.setVisibleXRangeMinimum(20000);
                rriGraph.moveViewToX(passtime);
            }
        });
    }
}
