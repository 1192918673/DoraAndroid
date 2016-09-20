package coms.geeknewbee.doraemon.communicate.socket;

import android.support.v4.view.animation.FastOutLinearInInterpolator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.TreeMap;

/**
 * Created by GYY on 2016/9/19.
 */
public class ReadInfoThread implements Runnable {

    private Socket socket;
    private BufferedReader in;

    private String content = "";
    private boolean isExit;

    public ReadInfoThread(Socket socket) {
        this.socket = socket;
        isExit = false;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  不断读取服务器发来的消息
    @Override
    public void run() {
        try {
            while (!isExit) {
                //  socket连接成功
                if (!socket.isClosed() && socket.isConnected()) {
                    if (!socket.isInputShutdown()) {
                        if ((content = in.readLine()) != null) {
                            content += "\n";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  停止线程
    public void stopThread() {
        isExit = true;
    }
}
