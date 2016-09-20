package coms.geeknewbee.doraemon.communicate.BLE;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by GYY on 2016/9/14.
 */
public class BLEData {
    BluetoothGattCharacteristic characteristic;
    byte[] data;

    public BLEData(BluetoothGattCharacteristic characteristic, byte[] data) {
        this.characteristic = characteristic;
        this.data = data;
    }
}
