package com.example.develop.gapnotificationapp.Ble;

import android.content.Context;
import android.util.Log;
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
import com.example.develop.gapnotificationapp.util.BinaryInteger;
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
    private boolean flag = true;
    private Context _context;

    static class ViewHolder {
        @BindView(R.id.ble_device_item_device_name)
        TextView deviceName;
        @BindView(R.id.ble_device_item_mac_address)
        TextView macAddress;
        @BindView(R.id.ble_device_item_rssi)
        TextView RSSI;
        @BindView(R.id.ble_device_item_read)
        TextView readValue;
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

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // 新規表示のとき
        if (convertView == null) {
            convertView = _inflater.inflate(_layoutID, null);
            holder = new ViewHolder(convertView);
            holder.connectToggle.setOnClickListener(new ConnectClickListener());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BleViewItem item = _bleDevicesList.get(position);
        RxBleDevice device = item.device.getDevice();
        holder.deviceName.setText(device.getName());
        holder.macAddress.setText(device.getMacAddress());
        holder.RSSI.setText("RSSI : " + Integer.toString(item.rssi));
        holder.readValue.setText("Read : " + item.read);
        holder.connectToggle.setTag(position);
        _bleDevicesList.get(position).holder = holder;

        return convertView;
    }

    public void addScanResult(RxBleScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.
        for (int i = 0; i < _bleDevicesList.size(); i++) {

            if (_bleDevicesList.get(i).device.getDevice().equals(bleScanResult.getBleDevice())) {
                _bleDevicesList.get(i).rssi = bleScanResult.getRssi();
                _bleDevicesList.get(i).read = "";
                _bleDevicesList.get(i).holder.RSSI.setText("RSSI : " + Integer.toString(bleScanResult.getRssi()));
                _bleDevicesList.get(i).holder.readValue.setText(_bleDevicesList.get(i).read);
                setBleType(_bleDevicesList.get(i));
                return;
            }
        }

        BleContent tmp = new BleContent(_context, bleScanResult.getBleDevice().getMacAddress());
        BleViewItem item = new BleViewItem(tmp, bleScanResult.getRssi(), "");
//        setBleType(item);
        _bleDevicesList.add(item);
        Collections.sort(_bleDevicesList, SORTING_COMPARATOR);
        notifyDataSetChanged();
    }

    private void setBleType(BleViewItem item){
        switch (GapNotificationApplication.getBleContentManager(_context).isRegistered(item.device)){
            case (BleContentManager.HEART_RATE):
                item.holder.type.setText( "心拍");
                break;
            case BleContentManager.EMG:
                item.holder.type.setText("筋電位");
                break;
            case BleContentManager.UNREGISTERED:
                item.holder.type.setText("未登録");
                break;
        }
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
        public String read;
        public int rssi;
        public ViewHolder holder;
        public void setType(String type) {
            holder.type.setText(type);
        }
        public BleViewItem(BleContent _device, int _rssi, String _read) {
            device = _device;
            read = _read;
            rssi = _rssi;
        }
    }
    // ソート用のラムダ式
    private static final Comparator<BleViewItem> SORTING_COMPARATOR = (lhs, rhs) -> {
        return lhs.device.getDevice().getName().compareTo(lhs.device.getDevice().getName());
    };
    public class ConnectClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            int position =(int)view.getTag();
            BleViewItem ble = ((BleViewItem)getItem(position));

            new MaterialDialog.Builder(_context)
                    .title(R.string.menu_title)
                    .items(R.array.sample_names)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch(text.toString()) {
                                case "心拍":
                                    GapNotificationApplication.getBleContentManager(_context).Deregistration(ble.device);
                                    GapNotificationApplication.getBleContentManager(_context).setHeartRate(ble.device);
                                    ble.setType(_context.getResources().getString(R.string.heart_rate));

                                    Toast.makeText(_context, "このデバイスを心拍に登録しました", Toast.LENGTH_LONG).show();
                                    break;
                                case "筋電":
                                    // ここに注文処理を記述。
                                    GapNotificationApplication.getBleContentManager(_context).Deregistration(ble.device);
                                    GapNotificationApplication.getBleContentManager(_context).setEMG(ble.device);
                                    ble.setType(_context.getResources().getString(R.string.emg));
                                    Toast.makeText(_context, "このデバイスを筋電位に登録しました", Toast.LENGTH_LONG).show();
                                    break;
                                case "解除":
                                    GapNotificationApplication.getBleContentManager(_context).Deregistration(ble.device);
                                    ble.setType(_context.getResources().getString(R.string.unregistered));
                                    Toast.makeText(_context, "このデバイスの登録を解除しました", Toast.LENGTH_LONG).show();
                                    break;
                            }
                            return true;
                        }
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

