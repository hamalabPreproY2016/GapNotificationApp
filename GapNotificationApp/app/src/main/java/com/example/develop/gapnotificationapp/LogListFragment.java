package com.example.develop.gapnotificationapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.develop.gapnotificationapp.Log.GapFileManager;
import com.example.develop.gapnotificationapp.Log.LogDirectoryListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogListFragment extends Fragment {

    @BindView(R.id.log_directory_list)
    public ListView list;
    public LogListFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_list, container, false);
        ButterKnife.bind(this, view);
        
        // Inflate the layout for this fragment
        GapFileManager gfm = new GapFileManager(getActivity());
        gfm.getNewLogDirectory();
        LogDirectoryListAdapter adapter = new LogDirectoryListAdapter(getActivity(), gfm);
        list.setAdapter(adapter);
        return view;
    }

}
