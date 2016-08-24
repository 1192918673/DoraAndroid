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

    /**
     * wifi信息
     */
    public WifiInfo wifiInfo;
    public static class WifiInfo{
        public int type;
        public String SSID;
        public String pwd;
    }
}
