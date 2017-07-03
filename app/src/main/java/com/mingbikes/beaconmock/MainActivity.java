package com.mingbikes.beaconmock;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mingbikes.beaconmock.bluetooth.BeaconManager;
import com.mingbikes.beaconmock.bluetooth.MockServerCallBack;

public class MainActivity extends AppCompatActivity {

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    class BleServerCallBack extends MockServerCallBack {

        @Override
        public void onServiceAdded(final int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        tv_status.setText("GATT_SUCCESS");
                    } else {
                        tv_status.setText("GATT_BREAK");
                    }
                }
            });
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, final int newState) {
            super.onConnectionStateChange(device, status, newState);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        tv_status.setText("已连接");
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        tv_status.setText("连接断开");
                    } else {
                        tv_status.setText("连接失败");
                    }
                }
            });

        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }
    }

    public void onStartClick(View view) {

        if (et_major.getText() == null || et_major.getText().length() == 0) {
            Toast.makeText(this, "存在输入参数major,请输入。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_minor.getText() == null || et_minor.getText().length() == 0) {
            Toast.makeText(this, "存在输入参数minor,请输入。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_tx_power.getText() == null || et_tx_power.getText().length() == 0) {
            Toast.makeText(this, "存在输入参数txPower,请输入。", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mBeaconManager == null) {
            mBeaconManager = new BeaconManager(this);
            mBeaconManager.init(999);
        }

        mBeaconManager.setMajor(Integer.valueOf(et_major.getText().toString()));
        mBeaconManager.setMinor(Integer.valueOf(et_minor.getText().toString()));
        mBeaconManager.setTxPower(Integer.valueOf(et_tx_power.getText().toString()));

        mBeaconManager.startAdvertising(new BleServerCallBack());
    }

    public void onStopClick(View view) {
        if (mBeaconManager != null) {
            mBeaconManager.stopAdvertising();
            mBeaconManager = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (mBeaconManager != null) {
            mBeaconManager.stopAdvertising();
        }
        super.onDestroy();
    }

    TextView tv_status;
    EditText et_major;
    EditText et_minor;
    EditText et_tx_power;

    private void initViews() {
        tv_status = (TextView) findViewById(R.id.tv_status);

        et_major = (EditText) findViewById(R.id.et_major);
        et_minor = (EditText) findViewById(R.id.et_minor);
        et_tx_power = (EditText) findViewById(R.id.et_tx_power);
    }
}
