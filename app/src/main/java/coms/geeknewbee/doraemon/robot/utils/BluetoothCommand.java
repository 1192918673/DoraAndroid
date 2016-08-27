package coms.geeknewbee.doraemon.robot.utils;

import android.net.wifi.WifiInfo;

/**
 * Created by zq on 2016/8/22 0022.
 */
public class BluetoothCommand {
    /**
     * action:机器人的动作
     */
    public String action;

    public BluetoothCommand() {
    }

    public BluetoothCommand(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    /**
     * wifi信息
     */
    public WifiInfo wifiInfo;
    public static class WifiInfo{
        public WifiInfo(int type, String SSID, String pwd) {
            this.type = type;
            this.SSID = SSID;
            this.pwd = pwd;
        }

        public int type;
        public String SSID;
        public String pwd;
    }
}
