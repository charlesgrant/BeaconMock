package com.mingbikes.beaconmock.utils;

import android.bluetooth.le.AdvertiseData;
import android.os.ParcelUuid;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * ble util
 */
public class BleUtil {

    //设置一下scan广播数据
    public static AdvertiseData createScanAdvertiseData(short major, short minor, byte txPower) {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeDeviceName(true);

        byte[] serverData = new byte[5];
        ByteBuffer bb = ByteBuffer.wrap(serverData);
        bb.order(ByteOrder.BIG_ENDIAN);
//        bb.put((byte) 0x02);
//        bb.put((byte) 0x15);
        bb.putShort(major);
        bb.putShort(minor);
        bb.put(txPower);

        builder.addServiceData(ParcelUuid.fromString(BluetoothUUID.bleServerUUID.toString())
                , serverData);

//        builder.setIncludeTxPowerLevel(true);
        AdvertiseData adv = builder.build();
        return adv;
    }

    /**
     * create AdvertiseDate for iBeacon
     */
    public static AdvertiseData createIBeaconAdvertiseData(UUID proximityUuid, short major, short minor, byte txPower) {
        if (proximityUuid == null) {
            throw new IllegalArgumentException("proximityUuid null");
        }
        // UUID to byte[]
        // ref. http://stackoverflow.com/questions/6881659/how-to-convert-two-longs-to-a-byte-array-how-to-convert-uuid-to-byte-array
//        byte[] manufacturerData = new byte[23];
//        ByteBuffer bb = ByteBuffer.wrap(manufacturerData);
//        bb.order(ByteOrder.BIG_ENDIAN);
//        // fixed 4bytes
//        //bb.put((byte) 0x4c);
//        //bb.put((byte) 0x00);
//        bb.put((byte) 0x02);
//        bb.put((byte) 0x15);
//        bb.putLong(proximityUuid.getMostSignificantBits());
//        bb.putLong(proximityUuid.getLeastSignificantBits());
//        bb.putShort(major);
//        bb.putShort(minor);
//        bb.put(txPower);

        String[] uuidstr = proximityUuid.toString().replaceAll("-", "").toLowerCase().split("");
        byte[] uuidBytes = new byte[16];
        for (int i = 1, x = 0; i < uuidstr.length; x++) {
            uuidBytes[x] = (byte) ((Integer.parseInt(uuidstr[i++], 16) << 4) | Integer.parseInt(uuidstr[i++], 16));
        }
        byte[] majorBytes = {(byte) (major >> 8), (byte) (major & 0xff)};
        byte[] minorBytes = {(byte) (minor >> 8), (byte) (minor & 0xff)};
        byte[] mPowerBytes = {txPower};
        byte[] manufacturerData = new byte[0x17];
        byte[] flagibeacon = {0x02, 0x15};

        System.arraycopy(flagibeacon, 0x0, manufacturerData, 0x0, 0x2);
        System.arraycopy(uuidBytes, 0x0, manufacturerData, 0x2, 0x10);
        System.arraycopy(majorBytes, 0x0, manufacturerData, 0x12, 0x2);
        System.arraycopy(minorBytes, 0x0, manufacturerData, 0x14, 0x2);
        System.arraycopy(mPowerBytes, 0x0, manufacturerData, 0x16, 0x1);
//
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addManufacturerData(0x004c, manufacturerData);

        AdvertiseData adv = builder.build();
        return adv;
    }
}
