package coms.geeknewbee.doraemon.robot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.communicate.BLE.BleManager;
import coms.geeknewbee.doraemon.communicate.IControl;
import coms.geeknewbee.doraemon.communicate.socket.SocketManager;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.robot.bean.BluetoothCommand;

/**
 * Created by GYY on 2016/9/20.
 */
public class TestActivity extends Activity implements View.OnClickListener, Runnable {

    public static final String EXTRA_BLE_MANAGER = "ble_manager";
    private EditText edW;
    private EditText edV;
    private Button btSend;
    private Button btStop;
    private boolean isExit;
    private boolean needSendData;
    private Thread sendThread;
    private int speechW;
    private int speechV;

    IControl iControl;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_foot);
        ip = getIntent().getStringExtra("ip");
        if (ip == null) {
            iControl = BleManager.getInstance();
        } else {
            iControl = SocketManager.getInstance();
        }
        edW = (EditText) findViewById(R.id.et_w);
        edV = (EditText) findViewById(R.id.et_v);
        btSend = (Button) findViewById(R.id.bt_send);
        btStop = (Button) findViewById(R.id.bt_stop);

        btSend.setOnClickListener(this);
        btStop.setOnClickListener(this);

        sendThread = new Thread(this);
        sendThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExit = true;
        sendThread.interrupt();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                needSendData = true;
                speechW = 0;
                speechV = 0;
                try {
                    speechW = Integer.parseInt(edW.getText().toString());
                    speechV = Integer.parseInt(edV.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_stop:
                needSendData = false;
                speechW = 0;
                speechV = 0;
                BluetoothCommand command = new BluetoothCommand();
                command.setBluetoothFootCommand(new BluetoothCommand.FootCommand(speechV, speechW));
                sendInfo(command);
                break;
        }
    }


    @Override
    public void run() {
        BluetoothCommand command = new BluetoothCommand();
        while (!isExit) {
            if (needSendData) {
                command.setBluetoothFootCommand(new BluetoothCommand.FootCommand(speechV, speechW));
                sendInfo(command);
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //  向设备发送信息
    private void sendInfo(BluetoothCommand command) {
        Gson gson = new Gson();
        String json = gson.toJson(command);
        String jsonCommand = "";
        if (ip == null) {
            jsonCommand = json;
        } else {
            jsonCommand = GlobalContants.SEND_SOCKET_CONTROL + json;
        }
        iControl.writeInfo(jsonCommand.getBytes(), 2);
    }
}
