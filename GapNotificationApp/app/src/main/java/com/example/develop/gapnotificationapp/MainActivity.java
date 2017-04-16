package com.example.develop.gapnotificationapp;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.develop.gapnotificationapp.voice.RealTimeVoiceSlicer;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        MainFragment newFragment = new MainFragment();
        fragmentTransaction.replace(R.id.container, newFragment).commit();

        checkPermission();

    }
    // パーミッションを確認
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 権限があればLocationManagerを取得
            Toast.makeText(MainActivity.this, "位置情報の取得は既に許可されています", Toast.LENGTH_SHORT).show();
        } else {
            // なければ権限を求めるダイアログを表示
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
//        // 位置情報
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 権限があればLocationManagerを取得
            Toast.makeText(MainActivity.this, "カメラの使用は既に許可されています", Toast.LENGTH_SHORT).show();
        } else {
            // なければ権限を求めるダイアログを表示
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                    2);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // 権限があればLocationManagerを取得
            Toast.makeText(MainActivity.this, "マイクへのアクセスは既に許可されています", Toast.LENGTH_SHORT).show();
        } else {
            // なければ権限を求めるダイアログを表示
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 3);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 権限があればLocationManagerを取得
            Toast.makeText(MainActivity.this, "ローカルファイルへのアクセスは既に許可されています", Toast.LENGTH_SHORT).show();
        } else {
            // なければ権限を求めるダイアログを表示
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
        }
    }
    // パーミッションダイアログの結果受取
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // パーミッションが必要な処理
                    Toast.makeText(MainActivity.this, "位置情報が許可されました", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "ざけんな許可しろよ　終了するわ", Toast.LENGTH_SHORT).show();
                    // パーミッションが得られなかった時
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            MainFragment newFragment = new MainFragment();
            fragmentTransaction.replace(R.id.container, newFragment).commit();
        } else if (id == R.id.nav_ble) {
            BLEFragment newFragment = new BLEFragment();
            fragmentTransaction.replace(R.id.container, newFragment).commit();
        } else if (id == R.id.nav_log_list) {
            LogListFragment newFragment = new LogListFragment();
            fragmentTransaction.replace(R.id.container, newFragment).commit();
        } else if (id == R.id.nav_camera) {
            CameraFragment newFragment = new CameraFragment();
            fragmentTransaction.replace(R.id.container, newFragment).commit();
        } else if (id == R.id.nav_experiment) {
            ExperimentFragment newFragment = new ExperimentFragment();
            fragmentTransaction.replace(R.id.container, newFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
