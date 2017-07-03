package com.mingbikes.beaconmock.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.mingbikes.beaconmock.utils.BluetoothUUID;

import java.util.UUID;

/**
 * gatt server callback
 * when the gatt service setup
 */
public class MockServerCallBack extends BluetoothGattServerCallback {

    private static final String TAG = "BleServer";
    private BluetoothGattServer mGattServer;
    private BluetoothDevice btClient;

    public void setupServices(BluetoothGattServer gattServer) throws InterruptedException {
        if (gattServer == null) {
            throw new IllegalArgumentException("gattServer is null");
        }
        mGattServer = gattServer;
        // 设置一个GattService以及BluetoothGattCharacteristic
        {
            //immediate alert
            BluetoothGattService service = new BluetoothGattService(UUID.fromString(BluetoothUUID.bleServerUUID.toString()),
                    BluetoothGattService.SERVICE_TYPE_PRIMARY);

            if (mGattServer != null && service != null) {
                mGattServer.addService(service);
            }
        }
    }

    //当添加一个GattService成功后会回调改接口。
    public void onServiceAdded(int status, BluetoothGattService service) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "onServiceAdded status=GATT_SUCCESS service=" + service.getUuid().toString());
        } else {
            Log.d(TAG, "onServiceAdded status!=GATT_SUCCESS");
        }
    }

    //BLE连接状态改变后回调的接口
    public void onConnectionStateChange(android.bluetooth.BluetoothDevice device, int status,
                                        int newState) {
        Log.e(TAG, String.format("1.onConnectionStateChange：device name = %s, address = %s"
                , device.getName(), device.getAddress()));
        Log.d(TAG, "onConnectionStateChange status=" + status + "->" + newState);
    }

    //当有客户端来读数据时回调的接口

    /**
     * 5.特征被读取。当回复响应成功后，客户端会读取然后触发本方法
     */
    public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device,
                                            int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        Log.e(TAG, String.format("1.onCharacteristicReadRequest：device name = %s, address = %s"
                , device.getName(), device.getAddress()));
        Log.e(TAG, String.format("onCharacteristicReadRequest：requestId = %s, offset = %s", requestId, offset));
        mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
    }

    //当有客户端来写数据时回调的接口
    /**
     * 2.描述被写入时，在这里执行
     * bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS...  收
     * 触发 onCharacteristicWriteRequest
    */
    @Override
    public void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice device,
                                             int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite,
                                             boolean responseNeeded, int offset, byte[] value) {
        mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
    }

    //当有客户端来写Descriptor时回调的接口
    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

        btClient = device;
        Log.d(TAG, "onDescriptorWriteRequest");
        // now tell the connected device that this was all successfull
        mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
    }
}