package coms.geeknewbee.doraemon.robot.utils;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import coms.geeknewbee.doraemon.robot.RobotWifiActivity;

/**
 * Created by 马庆军 on 16/5/17.
 */
public class BluetoothConnectActivityReceiver extends BroadcastReceiver {
    public static boolean btIsCancel=false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(
                "android.bluetooth.device.action.PAIRING_REQUEST")) {
            BluetoothDevice btDevice = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");
            // device.setPin(pinBytes);
            com.lidroid.xutils.util.LogUtils.e("BluetoothConnectActivityReceiver onReceive");
            try {
                ClsUtils.setPin(btDevice.getClass(), btDevice, RobotWifiActivity.strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(btDevice.getClass(), btDevice);
                ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
                btIsCancel=true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }
}