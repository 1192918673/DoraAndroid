package coms.geeknewbee.doraemon.communicate.BLE;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import coms.geeknewbee.doraemon.utils.ILog;

/**
 * BLE数据发送对象
 */
public class BleSender extends Thread {
    public static final int MAX_LENGTH = 18;
    private BlockingQueue<BLEData> datas;
    //    private Map<BluetoothGattCharacteristic, Queue<byte[]>> characteristicQueueMap;
    private BluetoothGatt mBluetoothGatt;
    private boolean isExit;
    private Thread sendThread;

    public BleSender() {
        datas = new ArrayBlockingQueue<BLEData>(100);
    }

    public void init(BluetoothGatt mBluetoothGatt) {
        clearAllData();
//        characteristicQueueMap = new HashMap<>();
        this.mBluetoothGatt = mBluetoothGatt;
        isExit = false;
        ILog.e("开启run");
        sendThread = new Thread(this);
        sendThread.start();
    }

    public void addData(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        if (bytes.length == 0)
            return;
        //分包
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

    public void stopSend() {
        clearAllData();
        isExit = true;
        if (sendThread != null) {
            sendThread.interrupt();
        }

    }

    private void sendData(BLEData data) {
        BluetoothGattCharacteristic characteristic = data.characteristic;
        byte[] bytes = data.data;
        if (characteristic == null || bytes == null)
            return;
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        characteristic.setValue(bytes);
        ILog.e("BleSender:向设备写信息：" + bytes);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private void clearAllData() {
        datas.clear();
    }

    @Override
    public void run() {
        super.run();
        ILog.e("执行run");
        while (!isExit) {
            try {
                BLEData data = datas.take();
                Thread.sleep(20);
                sendData(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ILog.e("send data thread complete");
    }
}