package com.example.develop.gapnotificationapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.polidea.rxandroidble.exceptions.BleScanException;

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
                            },
                            throwable -> {
                                if (throwable instanceof BleScanException) {
                                    handleBleScanException((BleScanException) throwable);
                                }
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
    private void handleBleScanException(BleScanException bleScanException) {

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                Toast.makeText(getContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.BLUETOOTH_DISABLED:
                Toast.makeText(getContext(), "Enable bluetooth and try again", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                Toast.makeText(getContext(),
                        "On Android 6.0 location permission is required. Implement Runtime Permissions", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                Toast.makeText(getContext(), "Location services needs to be enabled on Android 6.0", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                Toast.makeText(getContext(), "Unable to start scanning", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
