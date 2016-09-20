package coms.geeknewbee.doraemon.communicate.BLE;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by GYY on 2016/9/12.
 */
public class BleRead {
    public static final String EMPTY = "";
    private Map<BluetoothGattCharacteristic, String> dataMap;

    public BleRead() {
        dataMap = new HashMap<>();
    }

    public synchronized String read(BluetoothGattCharacteristic characteristic) {
        //  读取到设备的提供的信息byte[]
        byte[] info = characteristic.getValue();
        //  将byte[] 转为JSON类型的字符串
        String temp = new String(info);

        ILog.e("读取到的结果：" + temp);

        if (TextUtils.isEmpty(temp)) {
            return EMPTY;
        }
        String result;
        if (dataMap.containsKey(characteristic))
            result = dataMap.get(characteristic);
        else
            result = EMPTY;

        result += temp;
        ILog.e("拼接结果：" + result);

        dataMap.put(characteristic, result);

        if (isComplete(result)) {
            ILog.e("完成:" + result);
            dataMap.put(characteristic, EMPTY);
            return result.substring(GlobalContants.COMMAND_ROBOT_PREFIX.length(), result.length() - GlobalContants.COMMAND_ROBOT_SUFFIX.length());
        } else
            return EMPTY;
    }

    private boolean isComplete(String str) {
        return str.startsWith(GlobalContants.COMMAND_ROBOT_PREFIX) && str.endsWith(GlobalContants.COMMAND_ROBOT_SUFFIX);
    }

    public void clearData() {
        dataMap.clear();
    }
}
