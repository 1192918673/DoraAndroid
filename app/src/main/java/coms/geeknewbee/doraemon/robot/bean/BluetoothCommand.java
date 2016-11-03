package coms.geeknewbee.doraemon.robot.bean;

import java.util.List;

/**
 * Created by zq on 2016/8/22 0022.
 */
public class BluetoothCommand {
    /**
     * action:机器人的动作
     */
    public String action;

    /**
     * ONLYFOOT命令
     */
    private LimbCommand limbCommand;

    public BluetoothCommand() {
    }

    public void setLimbCommand(LimbCommand limbCommand) {
        this.limbCommand = limbCommand;
    }

    public BluetoothCommand(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    /**
     * wifi信息
     */
    public WifiInfo wifiInfo;

    public static class WifiInfo {
        public WifiInfo(int type, String SSID, String pwd) {
            this.type = type;
            this.SSID = SSID;
            this.pwd = pwd;
        }

        public int type;
        public String SSID;
        public String pwd;
    }

    /**
     * 手动控制脚步
     */
    private FootCommand bluetoothFootCommand;

    /**
     * 表情名字
     */
    private String faceName;

    /**
     * 表情的循环次数
     */
    private int loop;

    /**
     * 声音
     */
    private String sound;
    /**
     * 动作脚本
     */
    private List<String> lines;

    public void setFaceName(String faceName) {
        this.faceName = faceName;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public void setBluetoothFootCommand(FootCommand bluetoothFootCommand) {
        this.bluetoothFootCommand = bluetoothFootCommand;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }


    public static class LimbCommand {

        /**
         * 脚步动作
         */
        private FootCommand footCommand;

        public void setFootCommand(FootCommand footCommand) {
            this.footCommand = footCommand;
        }


    }

    public static class FootCommand {

        public FootCommand(int v, int w) {
            this.v = v;
            this.w = w;
        }

        public FootCommand(int v, int w, int duration) {
            this.v = v;
            this.w = w;
            this.duration = duration;
        }


        public int v;
        public int w;
        /**
         * 持续时间 ms
         */
        public int duration = 0;
    }
}
