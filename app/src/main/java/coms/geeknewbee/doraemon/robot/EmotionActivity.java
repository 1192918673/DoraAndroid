package coms.geeknewbee.doraemon.robot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.communicate.BLE.BleManager;
import coms.geeknewbee.doraemon.communicate.IControl;
import coms.geeknewbee.doraemon.communicate.socket.SocketManager;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.robot.utils.BluetoothCommand;
import coms.geeknewbee.doraemon.utils.ILog;

public class EmotionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    // 表情图片资源
    private int[] img = {R.mipmap.tong_yi, R.mipmap.bu_tong_yi, R.mipmap.gao_xing, R.mipmap.ji_dong, R.mipmap.hai_pa,
            R.mipmap.yi_wen, R.mipmap.yun, R.mipmap.tao_xin, R.mipmap.fa_dai, R.mipmap.zai_jian,
            R.mipmap.hai_xiu, R.mipmap.ku, R.mipmap.han, R.mipmap.jing_ya, R.mipmap.an_wei,
            R.mipmap.gao_xing, R.mipmap.fa_dai, R.mipmap.yi_wen, R.mipmap.ji_dong, R.mipmap.huan_ying};
    // 表情文本资源
    private String[] des = {"同意", "不同意", "高兴", "激动", "疑问", "晕", "桃心", "发呆", "害羞", "哭",
            "汗", "惊讶", "得意", "白眼", "萌", "愤怒", "害怕", "再见", "安慰", "欢迎"};
    // GridView条目集合
    private List<Map<String, Object>> items;

    IControl iControl;
    private String ip;
    private List<String> lines;

    private final int MSG_WHAT_SOCKET_DISCONNECT = 1001;
    private static final int MSG_DIS_CONNET = 800;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_SOCKET_DISCONNECT:    //socket连接断开
                case MSG_DIS_CONNET:    //蓝牙连接已断开
                    ILog.e("连接已断开");
                    tt.showMessage("连接已断开，将要关闭当前页面", tt.SHORT);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);
        ip = getIntent().getStringExtra("ip");
        if (ip == null) {
            iControl = BleManager.getInstance();
        } else {
            iControl = SocketManager.getInstance();
        }
        iControl.init(handler, this);
        ImageButton ibBack = (ImageButton) findViewById(R.id.ibBack);
        GridView gview = (GridView) findViewById(R.id.gview);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        gview.setOnItemClickListener(this);
        items = new ArrayList<>();
        lines = new ArrayList<>();
        initData();
        String[] from = new String[]{"image", "desc"};
        int[] to = new int[]{R.id.item_img, R.id.item_des};
        SimpleAdapter mSimAdapter = new SimpleAdapter(this, items, R.layout.item_gview, from, to);
        gview.setAdapter(mSimAdapter);
    }

    // 初始化条目集合数据
    private List<Map<String, Object>> initData() {
        for (int i = 0; i < img.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", img[i]);
            map.put("desc", des[i]);
            items.add(map);
        }
        return items;
    }

    //初始化动作集合
    private void initLines(int rawId) {
        //清空动作集合
        lines.clear();
        //获取raw文件输入流
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(rawId)));
        String line;
        try {
            while ((line = bufReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //创建发送命令
        BluetoothCommand command = new BluetoothCommand();
        command.setFaceName(GlobalContants.emotion[position]);
        command.setSound(GlobalContants.sound[position]);
//        initLines(GlobalContants.action[position]);
//        command.setLines(lines);
        Gson gson = new Gson();
        String json = gson.toJson(command);
        String jsonCommand = "";
        //根据使用的控制方式决定前后缀及功能码
        if (ip == null) {
            jsonCommand = GlobalContants.COMMAND_ROBOT_PREFIX + json + GlobalContants.COMMAND_ROBOT_SUFFIX;
        } else {
            jsonCommand = GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET + GlobalContants.SEND_SOCKET_CONTROL
                    + json + GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET;
        }
        ILog.e("发送数据：" + jsonCommand);
        tt.showMessage("正在发送命令", tt.LONG);
        iControl.writeInfo(jsonCommand.getBytes(), 2);
    }
}