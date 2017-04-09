package com.example.develop.gapnotificationapp.Log;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.develop.gapnotificationapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ragro on 2017/04/08.
 */

public class LogDirectoryListAdapter extends BaseAdapter {
    public Context _context;
    public LayoutInflater _inflater;
    public int layoutID;
    public GapFileManager _mager;
    public List<File> list = new ArrayList<File>();
    public SimpleDateFormat _format;
    static class ViewHolder {
        TextView date;
        TextView time;
        ImageButton view;
        ImageButton upload;
        ImageButton delete;
    }
    public LogDirectoryListAdapter(Context context, GapFileManager manager){
        _context = context;
        _inflater = LayoutInflater.from(_context);
        layoutID = R.layout.log_directory_item;
        _mager = manager;
        _format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        reload();
    }
    public class CustomComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            return Long.compare(o2.lastModified(), o1.lastModified());
        }
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void reload(){
        list = Arrays.asList(_mager.getLogDirectories());
        Collections.sort(list, new CustomComparator());
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = _inflater.inflate(layoutID, null);
            holder = new ViewHolder();
            holder.date = (TextView)view.findViewById(R.id.log_list_item_date);
            holder.time = (TextView)view.findViewById(R.id.log_list_item_time);
            holder.upload = (ImageButton) view.findViewById(R.id.directory_upload);
            holder.upload.setTag(list.get(i).getName());
            holder.delete = (ImageButton) view.findViewById(R.id.directory_delete);
            holder.delete.setTag(list.get(i).getName());
            holder.view = (ImageButton)view.findViewById(R.id.directory_view);
            holder.view.setTag(list.get(i).getName());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("GapFileManger", "click view (" + (String) view.getTag() + ")");
                }
            });

            holder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                    builder.setTitle("確認")
                            .setMessage("この実験記録をGoogleDriveにアップロードしますか")
                            .setPositiveButton("OK", new UploadDialogClickListener((String) view.getTag()))
                            .setNegativeButton("Cancel", null)
                            .show();

                    AlertDialog dialog = builder.create();
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                    builder.setTitle("確認")
                            .setMessage("この実験記録を削除しますか")
                            .setPositiveButton("OK", new DeleteDialogClickListener((String) view.getTag()))
                            .setNegativeButton("Cancel", null)
                            .show();
                    AlertDialog dialog = builder.create();
                }
            });



            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.date.setText(_context.getString(R.string.directory_name, list.get(i).getName()));


        return view;
    }

    public class DeleteDialogClickListener implements DialogInterface.OnClickListener{
        private String _directory_name;
        public DeleteDialogClickListener(String directory_name){
            _directory_name = directory_name;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            _mager.deleteLogDirectory(_directory_name);
            reload();
        }
    }
    public class UploadDialogClickListener implements DialogInterface.OnClickListener{
        private String _directory_name;
        public UploadDialogClickListener(String directory_name){
            _directory_name = directory_name;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    }
}
