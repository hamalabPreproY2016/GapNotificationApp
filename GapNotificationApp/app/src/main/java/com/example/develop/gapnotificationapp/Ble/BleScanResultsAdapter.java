package com.example.develop.gapnotificationapp.Ble;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.develop.gapnotificationapp.R;
import com.example.develop.gapnotificationapp.util.BinaryInteger;
import com.example.develop.gapnotificationapp.util.HexString;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by ragro on 2017/03/28.
 */

public class BleScanResultsAdapter extends BaseAdapter {
    private LayoutInflater _inflater;
    private int _layoutID;
    private List<BleViewItem> _bleDevicesList= new ArrayList<BleViewItem>();
    private BleContent _ble0;
    private boolean flag = true;

    static class ViewHolder {
        TextView deviceName;
        TextView macAddress;
        TextView RSSI;
        TextView readValue;
        Button connectToggle;
    }

    public BleScanResultsAdapter(Context context){

        _inflater = LayoutInflater.from(context);
        _layoutID = R.layout.ble_scan_results_item;

        _ble0 = new BleContent(context,
                context.getResources().getString(R.string.ble_0_mac_adress),
                UUID.fromString(context.getResources().getString(R.string.uuid_write)),
                UUID.fromString(context.getResources().getString(R.string.uuid_notify)));

        _ble0.setNotificationListener(new NotificationListener() {
            @Override
            public void getNotification(byte[] bytes) {
                int num = BinaryInteger.TwoByteToInteger(bytes);
                Log.d("BLECONNTENT", Integer.toString(num));
            }
        });
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // 新規表示のとき
        if (convertView == null) {
            convertView = _inflater.inflate(_layoutID, null);
            holder = new ViewHolder();
            holder.deviceName = (TextView)convertView.findViewById(R.id.ble_device_item_device_name);
            holder.macAddress = (TextView)convertView.findViewById(R.id.ble_device_item_mac_address);
            holder.RSSI = (TextView)convertView.findViewById(R.id.ble_device_item_rssi);
            holder.readValue = (TextView)convertView.findViewById(R.id.ble_device_item_read);
            holder.connectToggle = (Button)convertView.findViewById(R.id.ble_device_item_toggle);
            holder.connectToggle.setOnClickListener(new ConnectClickListener());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BleViewItem item = _bleDevicesList.get(position);
        RxBleDevice device = item.device;
        holder.deviceName.setText(device.getName());
        holder.macAddress.setText(device.getMacAddress());
        holder.RSSI.setText("RSSI : " + Integer.toString(item.rssi));
        holder.readValue.setText("Read : " + item.read);
        holder.connectToggle.setTag(device.getMacAddress());
        _bleDevicesList.get(position).holder = holder;

        return convertView;
    }

    public void addScanResult(RxBleScanResult bleScanResult) {
            // Not the best way to ensure distinct devices, just for sake on the demo.

            for (int i = 0; i < _bleDevicesList.size(); i++) {

                if (_bleDevicesList.get(i).device.equals(bleScanResult.getBleDevice())) {
                    _bleDevicesList.get(i).rssi = bleScanResult.getRssi();
                    _bleDevicesList.get(i).read = "";
                    _bleDevicesList.get(i).holder.RSSI.setText("RSSI : " + Integer.toString(bleScanResult.getRssi()));
                    return;
                }
            }

            _bleDevicesList.add(new BleViewItem(bleScanResult.getBleDevice(), bleScanResult.getRssi(), ""));
            Collections.sort(_bleDevicesList, SORTING_COMPARATOR);
            notifyDataSetChanged();
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
        public RxBleDevice device;
        public String read;
        public int rssi;
        public ViewHolder holder;
        public BleViewItem(RxBleDevice _device, int _rssi, String _read) {
            device = _device;
            read = _read;
            rssi = _rssi;
        }
    }
    // ソート用のラムダ式
    private static final Comparator<BleViewItem> SORTING_COMPARATOR = (lhs, rhs) -> {
        return lhs.device.getName().compareTo(rhs.device.getName());
    };
    public class ConnectClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String address =(String)view.getTag();
            if(_ble0.Connected()){
                ((Button)view).setText("CONNECT");
                _ble0.DisConnect();
            }else {
                Log.d(_ble0.TAG, "Connect");
                ((Button) view).setText("DISCONNECT");
                _ble0.Connect();
            }
        }
    }


}

