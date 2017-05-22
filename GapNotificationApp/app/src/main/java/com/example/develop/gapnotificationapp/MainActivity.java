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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.develop.gapnotificationapp.voice.RealTimeVoiceSlicer;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int current_flagment_id;

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

        current_flagment_id = R.id.nav_main;
        MainFragment newFragment = new MainFragment();
        fragmentTransaction.replace(R.id.container, newFragment).commit();
        MainActivityPermissionsDispatcher.permissionWithCheck(MainActivity.this);

    }
    @NeedsPermission({
            Manifest.permission.CAMERA ,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, })
    // パーミッションを確認
    public void permission() {
    }
    @OnPermissionDenied({
            Manifest.permission.CAMERA ,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE })
    public void deniedPermission(){
    }
    @SuppressWarnings("unused")
    @OnShowRationale({
            Manifest.permission.CAMERA ,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE })
    public void showRationalForStorage(final PermissionRequest request){
        request.proceed();
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 現在のフラグメントIDを保持
        current_flagment_id = id;

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
