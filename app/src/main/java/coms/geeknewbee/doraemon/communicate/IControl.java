package coms.geeknewbee.doraemon.communicate;

import android.content.Context;
import android.os.Handler;

/**
 * Created by GYY on 2016/9/19.
 */
public interface IControl {

    //  初始化方法
    boolean init(Handler handler, Context context);

    //  开始扫描蓝牙设备
    void startScan();

    //  停止扫描蓝牙设备
    void stopScan();

    /**
     * 连接的方法
     * @param ip socket连接传入ip，蓝牙连接传入null
     */
    void connect(String ip);

    /**
     * 写信息的方法
     * @param data  写入的信息
     * @param type  发送的类型，1：设置wifi；2：发送控制命令
     */
    void writeInfo(String data, int type);

    //  关闭连接
    void close();
}
