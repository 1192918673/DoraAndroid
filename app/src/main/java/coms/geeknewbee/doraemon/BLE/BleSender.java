package coms.geeknewbee.doraemon.BLE;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.text.TextUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import coms.geeknewbee.doraemon.utils.ILog;

/**
 * BLE数据发送对象
 */
public class BleSender {
    public static final int MAX_LENGTH = 18;
    private Map<BluetoothGattCharacteristic, Queue<byte[]>> characteristicQueueMap;
    private BluetoothGatt mBluetoothGatt;

    public void init(BluetoothGatt mBluetoothGatt) {
        clearAllData();
        characteristicQueueMap = new HashMap<>();
        this.mBluetoothGatt = mBluetoothGatt;
    }

    public synchronized void addData(BluetoothGattCharacteristic characteristic, String data) {
        if (TextUtils.isEmpty(data))
            return;

        Queue<byte[]> queue = characteristicQueueMap.get(characteristic);
        if (queue == null)
            queue = new ArrayDeque<>();

        //当前队列是空的时候，在添加完队列后需要发送一次数据。否则由onCharacteristicWrite 触发发送
        boolean needSendDataAfterAddData = queue.size() == 0;

        ILog.e(" queue.size:" + queue.size());
        //分包
        byte[] bytes = data.getBytes();
        int length = bytes.length;
        int number = length % MAX_LENGTH == 0 ? length / MAX_LENGTH : length / MAX_LENGTH + 1;
        for (int i = 0; i < number; i++) {
            byte[] range = Arrays.copyOfRange(bytes, i * 18, i == number - 1 ? length : (i + 1) * MAX_LENGTH);
            queue.offer(range);
        }

        if (!characteristicQueueMap.containsKey(characteristic)) {
            characteristicQueueMap.put(characteristic, queue);
        }

        if (needSendDataAfterAddData)
            sendNextPackage(characteristic);
    }

    public synchronized void sendNextPackage(BluetoothGattCharacteristic characteristic) {
        Queue<byte[]> queue = characteristicQueueMap.get(characteristic);
        byte[] bytes = queue.poll();
        if (bytes == null)
            return;
        characteristic.setValue(bytes);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public synchronized void clearAllData() {
        if (characteristicQueueMap != null)
            characteristicQueueMap.clear();
    }
}
