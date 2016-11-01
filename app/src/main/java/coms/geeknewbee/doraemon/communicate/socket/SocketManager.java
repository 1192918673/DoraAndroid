package coms.geeknewbee.doraemon.communicate.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import coms.geeknewbee.doraemon.communicate.IControl;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.utils.I2BUtils;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by GYY on 2016/9/19.
 */
public class SocketManager implements IControl, ReadInfoThread.onReceiveDataListener {

    private Handler handler;
    private Socket socket;
    private BufferedOutputStream out;
    private final int MSG_WHAT_SOCKET_CONNECT = 1000;
    private final int MSG_WHAT_SOCKET_DISCONNECT = 1001;

    private final int MSG_WHAT_START_ADD_FACE = 1002;
    private final int MSG_WHAT_ADD_FACE = 1003;
    private final int MSG_WHAT_ADD_NAME = 1004;
    private final int MSG_WHAT_ADD_PHOTO = 1005;
    private final int MSG_WHAT_CONNECT_FAILED = 1006;

    private ReadInfoThread runnable;
    private Thread thread;
    private ExecutorService singleThreadExecutor;
    private static SocketManager socketManager = new SocketManager();

    private SocketManager() {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static SocketManager getInstance() {
        return socketManager;
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
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, GlobalContants.PORT), 10000);
                    out = new BufferedOutputStream(socket.getOutputStream());
                    ILog.e("socket连接成功");
                    Message msg = Message.obtain();
                    msg.what = MSG_WHAT_SOCKET_CONNECT;
                    handler.sendMessage(msg);
                    //  创建线程不断地读取消息
                    runnable = new ReadInfoThread(socket);
                    runnable.setOnReceiveDataListener(SocketManager.this);
                    thread = new Thread(runnable);
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = MSG_WHAT_CONNECT_FAILED;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    @Override
    public void writeInfo(byte[] data, int type) {
        byte[] length = I2BUtils.int2bytes(data.length);
        byte[] prefix = GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET.getBytes();
        byte[] suffix = GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET.getBytes();
        final byte[] sendData = new byte[prefix.length + data.length + suffix.length];
        System.arraycopy(prefix, 0, sendData, 0, prefix.length);
        System.arraycopy(data, 0, sendData, prefix.length, data.length);
        System.arraycopy(suffix, 0, sendData, prefix.length + data.length, suffix.length);

        ILog.e("发送信息：");
        singleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!socket.isClosed() && socket.isConnected() && !socket.isOutputShutdown()) {
                        ILog.e("向流中写数据");
                        //  将数据写入流中并向服务器端发送
                        out.write(sendData);
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
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                ILog.e("关闭socket");
                socket.close();
                socket = null;
                if (runnable != null && thread != null) {
                    runnable.stopThread();
                    thread.interrupted();
                }
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

    @Override
    public void onReceiveData(String data) {
        Message msg = Message.obtain();
        String code2 = 2 + "";
        String code3 = 3 + "";
        String code4 = 4 + "";
        String code5 = 5 + "";
        if (data == null) { //连接断开的消息
            msg.what = MSG_WHAT_SOCKET_DISCONNECT;
        } else if (data.startsWith(code2)) {    //开始录入人脸
            msg.what = MSG_WHAT_START_ADD_FACE;
            msg.obj = data.substring(data.length() - code2.length());
        } else if (data.startsWith(code3)) {    //录入人脸是否成功
            msg.what = MSG_WHAT_ADD_FACE;
            msg.obj = data.substring(data.length() - code3.length());
        } else if (data.startsWith(code4)) {    //人名是否添加成功
            msg.what = MSG_WHAT_ADD_NAME;
            msg.obj = data.substring(data.length() - code4.length());
        } else if (data.startsWith(code5)) {    //照片是否添加成功
            msg.what = MSG_WHAT_ADD_PHOTO;
            msg.obj = data.substring(data.length() - code5.length());
        }
        handler.sendMessage(msg);
    }
}
