package com.mingbikes.beaconmock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mingbikes.beaconmock.bluetooth.BeaconManager;
import com.mingbikes.beaconmock.bluetooth.MockServerCallBack;

public class BluetoothReceiver extends BroadcastReceiver {

    /***
     * adb shell am broadcast -a com.mingbikes.beacon.mock.start --ei major 10001 --ei minor 12345
     */

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.e("BluetoothReceive", "===========action:" + action);

        if("com.mingbikes.beacon.mock.start".equals(action)) {

            int major = intent.getIntExtra("major", 10001);
            int minor = intent.getIntExtra("minor", 12345);

            Log.e("BluetoothReceive", "===========major:" + major);
            Log.e("BluetoothReceive", "===========minor:" + minor);

            BeaconManager.getInstance().setContext(context);
            BeaconManager.getInstance().setMajor(major);
            BeaconManager.getInstance().setMinor(minor);

            BeaconManager.getInstance().startAdvertising(new MockServerCallBack());

        } else if("com.mingbikes.beacon.mock.stop".equals(action)) {

            BeaconManager.getInstance().stopAdvertising();

        }
    }
}