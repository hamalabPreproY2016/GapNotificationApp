package com.example.develop.gapnotificationapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.develop.gapnotificationapp.Ble.BleScanResultsAdapter;
import com.example.develop.gapnotificationapp.dummy.DummyContent.DummyItem;
import com.polidea.rxandroidble.RxBleClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BLEFragment extends Fragment {
    @BindView(R.id.ble_scan_toggle)
    public Button _scanToggle;
    @BindView(R.id.ble_scan_results)
    public ListView _scanResults;
    public BleScanResultsAdapter _adapter;

    public RxBleClient _rxBleClient;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BLEFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BLEFragment newInstance(int columnCount) {
        BLEFragment fragment = new BLEFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ble_scan_results, container, false);
        // バターナイフとの連携
        ButterKnife.bind(this, view);
        _adapter = new BleScanResultsAdapter(getContext());
        _scanResults.setAdapter(_adapter);
        _rxBleClient = GapNotificationApplication.getRxBleClient(getContext());
        registerForContextMenu(_scanResults);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @OnClick(R.id.ble_scan_toggle)
    public void ScanToggle() {
        if (isScanning()) {
            this.clearSubscription();
        } else {
            scanSubscription = _rxBleClient.scanBleDevices()
                    .subscribe(
                            rxBleScanResult -> {
                                _adapter.addScanResult(rxBleScanResult);
//                                Log.d("D", "scan success");
                                scanSubscription.unsubscribe();
                            },
                            throwable -> {
                                // Handle an error here.
                                Log.d("D", "scan failed");
                            }
                    );

        }

        updateButtonUIState();
    }
    Subscription scanSubscription;
    //     スキャンが解除されたとき？
    private void clearSubscription () {
        scanSubscription.unsubscribe();
        scanSubscription = null;
        _adapter.clearScanResults();
        updateButtonUIState();
    }

    private boolean isScanning() {
        return scanSubscription != null;
    }
    // ボタンの表示を更新

    private void updateButtonUIState() {
        _scanToggle.setText(isScanning() ? R.string.ble_stop : R.string.ble_scan);
    }

    @OnClick(R.id.button_start_observe)
    public void pushObservationButton() {
        segueExperimentFragment();
    }

    private void segueGraphFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        GraphFragment newFragment = new GraphFragment();
        fragmentTransaction.replace(R.id.container, newFragment).commit();
    }

    private void segueExperimentFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        ExperimentFragment newFragment = new ExperimentFragment();
        fragmentTransaction.replace(R.id.container, newFragment).commit();
    }

    // コンテキストメニューの作成
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        // BleContentのタイプを設定するためのコンテキストメニューを作成
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.setting_ble_content_type_context, menu);  //menuリスト
        menu.setHeaderTitle(R.string.ble_scan_context_menu_header);  // タイトル
        menu.setHeaderIcon(android.R.drawable.ic_menu_info_details);  // アイコン
    }
    // コンテキストメニューを選択した時の処理
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();  //（1）
        int listPosition = info.position;
        Log.d("BLECONNTENT", Integer.toBinaryString(listPosition));
        BleScanResultsAdapter.BleViewItem ble = (BleScanResultsAdapter.BleViewItem)_adapter.getItem(listPosition);
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.bleContentContextHeartRate:
                GapNotificationApplication.getBleContentManager(getActivity()).Deregistration(ble.device);
                GapNotificationApplication.getBleContentManager(getActivity()).setHeartRate(ble.device);
                ble.setType(getActivity().getResources().getString(R.string.heart_rate));

                Toast.makeText(getActivity(), "このデバイスを心拍に登録しました", Toast.LENGTH_LONG).show();
                break;
            case R.id.bleContentContextEMG:
                // ここに注文処理を記述。
                GapNotificationApplication.getBleContentManager(getActivity()).Deregistration(ble.device);
                GapNotificationApplication.getBleContentManager(getActivity()).setEMG(ble.device);
                ble.setType(getActivity().getResources().getString(R.string.emg));
                Toast.makeText(getActivity(), "このデバイスを筋電位に登録しました", Toast.LENGTH_LONG).show();
                break;
            case R.id.bleContentDeregistration:
                GapNotificationApplication.getBleContentManager(getActivity()).Deregistration(ble.device);
                ble.setType(getActivity().getResources().getString(R.string.unregistered));
                Toast.makeText(getActivity(), "このデバイスの登録を解除しました", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }

}
