package com.example.develop.gapnotificationapp.Log;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ragro on 2017/04/08.
 */

public class GapFileManager {
    private String _root = "DataLog";
    private Context _context;
    private File _rootDirectory;
    private SimpleDateFormat format;
    public GapFileManager(Context context){
        _context = context;
        File applicationDirectory = context.getApplicationContext().getExternalFilesDir(null);
        _rootDirectory = new File(applicationDirectory, _root);
        Log.d("GapFileManger",_rootDirectory.toString());
        if(!_rootDirectory.exists() || !_rootDirectory.isDirectory()) {
            _rootDirectory.mkdir();
            Log.d("GapFileManger", "create new root directory");
        }
        format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    }
    // 新しいLogディレクトリを現在の日時で作成
    public File getNewLogDirectory(){
        File[] files = _rootDirectory.listFiles();
        Log.d("GapFileManger", _root + "/" +Integer.toString(files.length));
        File result = new File(_rootDirectory, format.format(new Date()));
        result.mkdir();
        return result;
    }
    // 全てのLogDirectoryを削除する
    public void clearAllLogDirectory(){
        Log.d("GapFileManger", Integer.toString(_rootDirectory.list().length));
        if (_rootDirectory.isDirectory())
        {
            String[] children = _rootDirectory.list();
            for (String child : children) {
                deleteLogDirectory(child);
            }
        }
    }
    // 指定の日付に対応するLogディレクトリを削除
    public  void deleteLogDirectory(String string){
        Log.d("GapFileManger", "delete : " + string);
        deleteRecursive(new File(_rootDirectory,string));
        // もしかしたら、ファイルが中に入っていたら消せないかも
    }
    // 対象内にあるファイル全てを削除
    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
    // Logディレクトリの数を取得
    public int getLogDirectoryNum(){
        return _rootDirectory.list().length;
    }
    // IDに対応するLogディレクトリを取得 無ければnullを返す
    public File getLogDirectory(int ID){

        File result = new File(_rootDirectory, Integer.toString(ID));
        if(!_rootDirectory.exists() || !_rootDirectory.isDirectory()) {
            return result;
        } else {
            return null;
        }
    }

    public File getLogDirectory(String ID){

        File result = new File(_rootDirectory, ID);
        if(_rootDirectory.exists() && _rootDirectory.isDirectory()) {
            return result;
        } else {
            return null;
        }
    }

    // 現在のLogディレクトリを全て取得
    public File[] getLogDirectories(){
        if (_rootDirectory.isDirectory())
        {
           return  _rootDirectory.listFiles();
        }
        return null;
    }
}
