package coms.geeknewbee.doraemon.communicate.socket;

import android.content.pm.ProviderInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutLinearInInterpolator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.TreeMap;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by GYY on 2016/9/19.
 */
public class ReadInfoThread implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private onReceiveDataListener onReceiveDataListener;

    private boolean isExit;

    private InputStream inputStream;

    public ReadInfoThread(Socket socket) {
        this.socket = socket;
        isExit = false;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnReceiveDataListener(ReadInfoThread.onReceiveDataListener onReceiveDataListener) {
        this.onReceiveDataListener = onReceiveDataListener;
    }

    //  不断读取服务器发来的消息
    @Override
    public void run() {
        byte[] bytes = new byte[1024];
        String data = "";
        while (!isExit) {
            try {
                int read = inputStream.read(bytes);
                if (read == -1) {
                    //TODO 连接断开
                    if (onReceiveDataListener != null) {
                        onReceiveDataListener.onReceiveData(null);
                    }
                    break;
                }
                data = new String(bytes, 0, read);
                if (data.startsWith(GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET) && data.endsWith(GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET)) {
                    data = data.substring(GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET.length(), data.length() - GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET.length());
                    ILog.e("收到消息：" + data);
                    if (onReceiveDataListener != null) {
                        onReceiveDataListener.onReceiveData(data);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                //TODO 连接断开
                if (onReceiveDataListener != null) {
                    onReceiveDataListener.onReceiveData(null);
                }
            }
        }
    }

    //  停止线程
    public void stopThread() {
        isExit = true;
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface onReceiveDataListener {
        void onReceiveData(String data);
    }

}
