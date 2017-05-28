package com.example.develop.gapnotificationapp.Ble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.develop.gapnotificationapp.GapNotificationApplication;
import com.example.develop.gapnotificationapp.R;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ragro on 2017/03/28.
 */

public class BleScanResultsAdapter extends BaseAdapter {
    private LayoutInflater _inflater;
    private int _layoutID;
    private List<BleViewItem> _bleDevicesList= new ArrayList<BleViewItem>();
    private Context _context;
    private BleContentManager _bleManager;

    static class ViewHolder {
        @BindView(R.id.ble_device_item_device_name)
        TextView deviceName;
        @BindView(R.id.ble_device_item_mac_address)
        TextView macAddress;
        @BindView(R.id.ble_device_item_rssi)
        TextView RSSI;
        @BindView(R.id.ble_device_item_type)
        TextView type;
        @BindView(R.id.ble_device_item_toggle)
        Button connectToggle;
        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    public BleScanResultsAdapter(Context context){
        _context = context;
        _inflater = LayoutInflater.from(context);
        _layoutID = R.layout.ble_scan_results_item;
        _bleManager = GapNotificationApplication.getBleContentManager(_context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // 新規表示のときはviewHolderを新規作成する
        if (convertView == null) {
            convertView = _inflater.inflate(_layoutID, null);
            holder = new ViewHolder(convertView);
            holder.connectToggle.setOnClickListener(new SetTypeClickListener());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BleViewItem item = _bleDevicesList.get(position);
        RxBleDevice device = item.device.getDevice();
        holder.deviceName.setText(device.getName());
        holder.macAddress.setText(device.getMacAddress());
        holder.RSSI.setText(_context.getResources().getString(R.string.ble_scan_rssi, item.rssi));
        holder.connectToggle.setTag(position);
        _bleDevicesList.get(position).holder = holder;

        return convertView;
    }

    // スキャンして発見したデバイスを追加してリストに表示
    // 既存の場合はデバイス情報を更新する
    public void addScanResult(RxBleScanResult bleScanResult) {
        // 既存の場合は内容を更新する
        for (int i = 0; i < _bleDevicesList.size(); i++) {

            if (_bleDevicesList.get(i).device.getDevice().equals(bleScanResult.getBleDevice())) {
                _bleDevicesList.get(i).rssi = bleScanResult.getRssi();
                _bleDevicesList.get(i).holder.RSSI.setText("RSSI : " + Integer.toString(bleScanResult.getRssi()));
                showBLERegistTypeView(_bleDevicesList.get(i));
                return;
            }
        }
        // 新規発見の場合はリストに追加する
        BleContent tmp = new BleContent(_context, bleScanResult.getBleDevice().getMacAddress());
        BleViewItem item = new BleViewItem(tmp, bleScanResult.getRssi());
        _bleDevicesList.add(item);
        Collections.sort(_bleDevicesList, SORTING_COMPARATOR);
        notifyDataSetChanged();
    }

    // BLEデバイスのタイプをUIに表示する
    private void showBLERegistTypeView(BleViewItem item){
        String type = _context.getResources().getStringArray(R.array.ble_set_type)[ _bleManager.isRegistered(item.device).getInt()];
        item.holder.type.setText(type);
    }

    @Override
    public int getCount() {
        return _bleDevicesList.size();
    }

    @Override
    public Object getItem(int position) {
        return _bleDevicesList.get(position);
    }

    public void clearScanResults() {
        _bleDevicesList.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class BleViewItem {
        public BleContent device;
        public int rssi;
        public ViewHolder holder;
        public BleViewItem(BleContent _device, int _rssi) {
            device = _device;
            rssi = _rssi;
        }
    }
    // ソート用のラムダ式
    private static final Comparator<BleViewItem> SORTING_COMPARATOR = (lhs, rhs) -> lhs.device.getDevice().getName().compareTo(lhs.device.getDevice().getName());

    // タイプボタンをセットした時のリスナー
    public class SetTypeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 選択したBLEデバイスを取得
            int position =(int)view.getTag();
            BleContent ble = ((BleViewItem)getItem(position)).device;
            // BLEデバイスを心拍・筋電・未登録のどれにセットするかを選択させる
            new MaterialDialog.Builder(_context)
                    .title(R.string.menu_title)
                    .items(R.array.ble_set_type)
                    .itemsCallbackSingleChoice(-1, (MaterialDialog dialog, View view2, int which, CharSequence text) ->{
                        _bleManager.Deregistration(ble);
                        switch (which){
                            case 0:
                                _bleManager.setHeartRate(ble);
                                break;
                            case 1:
                                _bleManager.setEMG(ble);
                                break;
                        }
                        // セットした旨を表示
                        String toastMessage = _context.getString(R.string.ble_scan_result_regist, text);
                        Toast.makeText(_context, toastMessage, Toast.LENGTH_LONG).show();
                        return true;
                    })
                    .positiveText("OK")
                    .show();
        }
    }
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}

