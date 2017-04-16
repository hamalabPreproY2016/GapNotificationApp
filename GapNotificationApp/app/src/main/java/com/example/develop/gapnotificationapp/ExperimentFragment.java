package com.example.develop.gapnotificationapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.develop.gapnotificationapp.R;
import com.example.develop.gapnotificationapp.experiment.ExperimentManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.develop.gapnotificationapp.R.id.view;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExperimentFragment extends Fragment {

    @BindView(R.id.experiment_start_button)
    Button _startButton ;

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

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.experiment_start_button)
    public void OnclickStart(){
        _expManager.Start();
    }

}
