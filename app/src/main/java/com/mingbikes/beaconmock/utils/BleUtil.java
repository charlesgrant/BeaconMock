package com.mingbikes.beaconmock.utils;

import android.bluetooth.le.AdvertiseData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * ble util
 */
public class BleUtil {

    /** create AdvertiseDate for iBeacon */
    public static AdvertiseData createIBeaconAdvertiseData(UUID proximityUuid, short major, short minor, byte txPower) {
        if (proximityUuid == null) {
            throw new IllegalArgumentException("proximityUuid null");
        }
        // UUID to byte[]
        // ref. http://stackoverflow.com/questions/6881659/how-to-convert-two-longs-to-a-byte-array-how-to-convert-uuid-to-byte-array
        byte[] manufacturerData = new byte[23];
        ByteBuffer bb = ByteBuffer.wrap(manufacturerData);
        bb.order(ByteOrder.BIG_ENDIAN);
        // fixed 4bytes
        //bb.put((byte) 0x4c);
        //bb.put((byte) 0x00);
        bb.put((byte) 0x02);
        bb.put((byte) 0x15);
        bb.putLong(proximityUuid.getMostSignificantBits());
        bb.putLong(proximityUuid.getLeastSignificantBits());
        bb.putShort(major);
        bb.putShort(minor);
        bb.put(txPower);

        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addManufacturerData(0x004c, manufacturerData);
        AdvertiseData adv = builder.build();
        return adv;
    }
}
