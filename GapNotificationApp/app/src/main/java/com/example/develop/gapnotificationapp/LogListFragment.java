package com.example.develop.gapnotificationapp;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.develop.gapnotificationapp.Log.GapFileManager;
import com.example.develop.gapnotificationapp.Log.LogDirectoryListAdapter;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogListFragment extends Fragment {

    private GapFileManager gfm;
    private LogDirectoryListAdapter adapter;

    @BindView(R.id.log_directory_list)
    public ListView list;
    public LogListFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_list, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);
        
        // Inflate the layout for this fragment
        gfm = new GapFileManager(getActivity());
        gfm.getNewLogDirectory();
        adapter = new LogDirectoryListAdapter(getActivity(), gfm, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("確認")
                        .setMessage("この実験記録を閲覧しますか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GapFileManager manager = new GapFileManager(getContext());
                                segueExperimentFragment(new File(manager.getLogDirectory((String)view.getTag()), "csv"));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                AlertDialog dialog = builder.create();
            }
        });
        list.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.option_menu_log, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_all_delete :
                // アラートダイアログを出してOKだった場合は全てのディレクトリを削除
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("確認")
                        .setMessage("この実験記録を削除しますか")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gfm.clearAllLogDirectory();
                                adapter.reload();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                AlertDialog dialog = builder.create();
                break;
        }
        return true;
    }

    private void segueExperimentFragment(File csvDir) {
        FragmentTransaction fragmentTransaction =  getFragmentManager().beginTransaction();

        ExperimentFragment newFragment = new ExperimentFragment();
        newFragment.csvDir = csvDir;

        fragmentTransaction.replace(R.id.container, newFragment).commit();
    }
}
