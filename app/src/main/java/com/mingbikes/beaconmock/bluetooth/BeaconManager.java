package com.mingbikes.beaconmock.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.mingbikes.beaconmock.utils.BleUtil;
import com.mingbikes.beaconmock.utils.BluetoothUUID;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * Beacon Manager
 * 1. setMajor
 * 2. setMinor
 * 3. setTxPower
 * 4. startAdvertising
 * 5. stopAdvertising
 *
 * Created by charles on 17/7/3.
 */

public class BeaconManager {

    public static final String TAG = "lock_mock_manager";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private Activity mActivity;
    private MockServerCallBack mMockServerCallBack;
    private BluetoothGattServer mGattServer;

    public BeaconManager(Activity activity) {
        mActivity = activity;

        //初始化BluetoothManager和BluetoothAdapter
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mActivity.getSystemService(BLUETOOTH_SERVICE);
        }

        if (mBluetoothManager != null && mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
    }

    public void init(int requestCode) {

        if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mActivity, "不支持ble", Toast.LENGTH_LONG).show();
            mActivity.finish();
            return;
        }

        final BluetoothManager mBluetoothManager = (BluetoothManager) mActivity.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(mActivity, "不支持ble", Toast.LENGTH_LONG).show();
            mActivity.finish();
            return;
        }

        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothAdvertiser == null) {
            Toast.makeText(mActivity, "the device not support peripheral", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "the device not support peripheral");
            mActivity.finish();
            return;
        }

        //打开蓝牙的套路
        if ((mBluetoothAdapter == null) || (!mBluetoothAdapter.isEnabled())) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, requestCode);
        }
    }

    private short mMajor = 0;
    private short mMinor = 0;
    private int mTxPower = 0;

    public void setMajor(int major) {
        mMajor = (short) major;
    }

    public void setMinor(int minor) {
        mMinor = (short) minor;
    }

    public void setTxPower(int txPower) {
        mTxPower = txPower;
    }

    public void startAdvertising(MockServerCallBack callBack) {
        //获取BluetoothLeAdvertiser，BLE发送BLE广播用的一个API
        if (mBluetoothAdvertiser == null) {
            mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }
        if (mBluetoothAdvertiser != null) {
            mMockServerCallBack = callBack;
            //打开BluetoothGattServer
            mGattServer = mBluetoothManager.openGattServer(mActivity, mMockServerCallBack);
            if (mGattServer == null) {
                Log.d(TAG, "gatt is null");
            }
            try {
                mMockServerCallBack.setupServices(mGattServer);
                //创建BLE beacon Advertising并且广播
                mBluetoothAdvertiser.startAdvertising(createAdvSettings(true, 0)
                        , BleUtil.createIBeaconAdvertiseData(BluetoothUUID.bleServerUUID,
                                mMajor, mMinor, (byte) 0x5b), mAdvCallback);
            } catch (Exception e) {
                Log.v(TAG, "Fail to setup BleService");
            }
        }
    }

    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        //设置广播的模式,应该是跟功耗相关
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setConnectable(connectable);
        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = builder.build();
        if (mAdvertiseSettings == null) {
            Log.e(TAG, "mAdvertiseSettings == null");
        }
        return mAdvertiseSettings;
    }

    //发送广播的回调，onStartSuccess/onStartFailure很明显的两个Callback
    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.d(TAG, "onStartSuccess, settingInEffect is null");
            }
        }

        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.d(TAG, "onStartFailure errorCode=" + errorCode);

            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                Toast.makeText(mActivity, "advertise_failed_data_too_large", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                Toast.makeText(mActivity, "advertise_failed_too_many_advertises", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising because no advertising instance is available.");

            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                Toast.makeText(mActivity, "advertise_failed_already_started", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertising is already started");

            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                Toast.makeText(mActivity, "advertise_failed_internal_error", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Operation failed due to an internal error");

            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                Toast.makeText(mActivity, "advertise_failed_feature_unsupported", Toast.LENGTH_LONG).show();
                Log.e(TAG, "This feature is not supported on this platform");

            }
        }
    };

    public void stopAdvertising() {
        //关闭BluetoothLeAdvertiser，BluetoothAdapter，BluetoothGattServer
        if (mBluetoothAdvertiser != null) {
            mBluetoothAdvertiser.stopAdvertising(mAdvCallback);
            mBluetoothAdvertiser = null;
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = null;
        }
        if (mGattServer != null) {
            mGattServer.clearServices();
            mGattServer.close();
        }
    }
}
