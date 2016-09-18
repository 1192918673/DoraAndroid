package coms.geeknewbee.doraemon.BLE;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * BLE数据发送对象
 */
public class BleSender extends Thread {
    public static final int MAX_LENGTH = 18;
    private BlockingQueue<BLEData> datas;
    //    private Map<BluetoothGattCharacteristic, Queue<byte[]>> characteristicQueueMap;
    private BluetoothGatt mBluetoothGatt;
    private boolean isRunning;
    private boolean isExit;

    public BleSender() {
        isRunning = false;
    }

    public void init(BluetoothGatt mBluetoothGatt) {
        clearAllData();
//        characteristicQueueMap = new HashMap<>();
        datas = new ArrayBlockingQueue<BLEData>(100);
        this.mBluetoothGatt = mBluetoothGatt;
        isExit = false;
        if (!isRunning) {
            start();
        }
    }

    public void addData(BluetoothGattCharacteristic characteristic, String data) {
        if (TextUtils.isEmpty(data))
            return;
        //分包
        byte[] bytes = data.getBytes();
        int length = bytes.length;
        int number = length % MAX_LENGTH == 0 ? length / MAX_LENGTH : length / MAX_LENGTH + 1;
        for (int i = 0; i < number; i++) {
            byte[] range = Arrays.copyOfRange(bytes, i * 18, i == number - 1 ? length : (i + 1) * MAX_LENGTH);
            try {
                //  将分包后的每个包的信息都填加到队列中
                datas.put(new BLEData(characteristic, range));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendData(BLEData data) {
        BluetoothGattCharacteristic characteristic = data.characteristic;
        byte[] bytes = data.data;
        if (characteristic == null || bytes == null)
            return;
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        characteristic.setValue(bytes);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void clearAllData() {
        if (datas != null)
            datas.clear();
    }

    @Override
    public void run() {
        super.run();
        isRunning = true;
        while (!isExit) {
            try {
                BLEData data = datas.take();
                Thread.sleep(20);
                sendData(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isRunning = false;
    }

    public void stopSend() {
        isExit = true;
    }
}