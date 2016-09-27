package coms.geeknewbee.doraemon.communicate.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import coms.geeknewbee.doraemon.communicate.IControl;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by GYY on 2016/9/19.
 */
public class SocketManager implements IControl {

    private Handler handler;
    private Socket socket;
    private BufferedOutputStream out;
    private final int MSG_WHAT_SOCKET_CONNECT = 1000;
    private ReadInfoThread runnable;
    private Thread thread;
    private ExecutorService singleThreadExecutor;

    public SocketManager() {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public boolean init(Handler handler, Context context) {
        this.handler = handler;
        return true;
    }

    @Override
    public void connect(final String ip) {
        ILog.e("ip:" + ip);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    socket = new Socket(ip, GlobalContants.PORT);
                    out = new BufferedOutputStream(socket.getOutputStream());
                    ILog.e("socket连接成功");
                    Message msg = Message.obtain();
                    msg.what = MSG_WHAT_SOCKET_CONNECT;
                    handler.sendMessage(msg);
                    //  创建线程不断地读取消息
                    runnable = new ReadInfoThread(socket);
                    thread = new Thread(runnable);
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void writeInfo(final String data, int type) {
        singleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!socket.isClosed() && socket.isConnected() && !socket.isOutputShutdown()) {
                        ILog.e("向流中写数据");
                        //  将数据写入流中并向服务器端发送
                        out.write(data.getBytes());
                        out.flush();
                        ILog.e("写入成功");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void close() {
        try {
            if (socket != null) {
                ILog.e("关闭socket");
                socket.close();
                socket = null;
                runnable.stopThread();
                thread.interrupted();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startScan() {
    }

    @Override
    public void stopScan() {
    }

}
