package coms.geeknewbee.doraemon.robot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.widget.RobotsView;

/**
 * Created by chen on 2016/4/6
 */
public class RobotActivity extends BaseActivity {

    /**
     * ---------------------组件--------------------
     **/
    private ImageButton ibBack;

    RobotsView robotsView;

    private TextView tvAdd;

    private RelativeLayout act_msg;

    private RelativeLayout act_robot;

    private RelativeLayout act_member;

    private RelativeLayout actOffline;

    private RelativeLayout actVoice;

    private RelativeLayout actEnterControl;

    LinearLayout ll_haveRobot;

    LinearLayout ll_null;

    /**
     * ---------------------数据--------------------
     **/
    RobotBean robot;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvAdd = (TextView) findViewById(R.id.tv_add);
        act_msg = (RelativeLayout) findViewById(R.id.act_msg);
        act_robot = (RelativeLayout) findViewById(R.id.act_robot);
        act_member = (RelativeLayout) findViewById(R.id.act_member);
        robotsView = (RobotsView) findViewById(R.id.robotsView);
        actOffline = (RelativeLayout) findViewById(R.id.actOffline);
        actVoice = (RelativeLayout) findViewById(R.id.actVoice);
        actEnterControl = (RelativeLayout) findViewById(R.id.actEnterControl);
        ll_haveRobot = (LinearLayout) findViewById(R.id.ll_haveRobot);
        ll_null = (LinearLayout) findViewById(R.id.ll_null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
        assignViews();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.contains("robotBeans")) {
            List<RobotBean> robotBeans =
                    (List<RobotBean>) session.get("robotBeans");
            setData(robotBeans);
        } else {
            setData(null);
        }
    }

    public void setData(List<RobotBean> robotBeans) {
        robotsView.setRobots(robotBeans);
        robotsView.setRobotKey(spt.getString(SptConfig.ROBOT_KEY, null));
        if (robotBeans == null || robotBeans.size() == 0) {
            ll_haveRobot.setVisibility(View.GONE);
            ll_null.setVisibility(View.VISIBLE);
        } else {
            ll_haveRobot.setVisibility(View.VISIBLE);
            ll_null.setVisibility(View.GONE);
            robot = robotsView.getRobot();
            act_msg.setOnClickListener(clickListener);
            act_robot.setOnClickListener(clickListener);
            act_member.setOnClickListener(clickListener);

            actOffline.setOnClickListener(clickListener);
            actEnterControl.setOnClickListener(clickListener);
            actVoice.setOnClickListener(clickListener);
        }
    }

    private void initListener() {
        ibBack.setOnClickListener(clickListener);
        tvAdd.setOnClickListener(clickListener);
    }

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final String serial_no = robotsView.getRobot().getSerial_no();
            switch (v.getId()) {
                case R.id.ib_back:
                    backOff();
                    break;
                case R.id.tv_add:
                    startActivity(new Intent(RobotActivity.this, RobotWifiActivity.class));
                    break;
                case R.id.act_msg:
                    Intent intent_msg = new Intent(RobotActivity.this, MessageNotifyActivity.class);
                    intent_msg.putExtra("robotId", robotsView.getRobot().getId());
                    startActivity(intent_msg);
                    break;
                case R.id.act_robot:
                    Intent intent_man = new Intent(RobotActivity.this, RobotManagerActivity.class);
                    intent_man.putExtra("robotPk", "" + robotsView.getRobot().getId());
                    startActivity(intent_man);
                    break;
                case R.id.act_member:
                    Intent intent_member = new Intent(RobotActivity.this, RobotMemberActivity.class);
                    intent_member.putExtra("robotPk", "" + robotsView.getRobot().getId());
                    startActivity(intent_member);
                    break;
                case R.id.actOffline:
                    Intent intentOffline = new Intent(RobotActivity.this, RobotControlActivity.class);
                    startActivity(intentOffline);
                    break;
                case R.id.actVoice:
                    Intent intent_voice = new Intent(RobotActivity.this, RobotVoiceActivity.class);
                    intent_voice.putExtra("robotPk", "" + robotsView.getRobot().getId());
                    startActivity(intent_voice);
                    break;
                case R.id.actEnterControl:
                    final String ip = spt.getString("ip_" + serial_no, null);
                    if (ip != null) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                String s = Ping(ip);
                                ILog.e("ping的结果：" + s);
                                if (s != null && s.equals("success")) {
                                    Intent intent_control = new Intent(RobotActivity.this, RobotControlActivity.class);
                                    intent_control.putExtra("ip", ip);
                                    startActivity(intent_control);
                                } else {
                                    Intent intent_wifi = new Intent(RobotActivity.this, RobotWifiActivity.class);
                                    intent_wifi.putExtra("type", "control");
                                    startActivity(intent_wifi);
                                }
                            }
                        }.start();
                    } else {
                        Intent intent_wifi = new Intent(RobotActivity.this, RobotWifiActivity.class);
                        intent_wifi.putExtra("type", "control");
                        startActivity(intent_wifi);
                    }
                    break;
            }
        }
    };

    public String Ping(String str) {
        String resault = "";
        Process p;
        try {
            //ping -c 3 -w 1  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 1  以秒为单位指定超时间隔，是指超时时间为1秒
            p = Runtime.getRuntime().exec("ping -c 1 -w 1 " + str);
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            ILog.e("Return ============" + buffer.toString());

            int status = p.waitFor();
            if (status == 0) {
                resault = "success";
            } else {
                resault = "faild";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resault;
    }

    /**
     * 返回到IndexActivity
     */
    public void backOff() {
        startActivity(new Intent(this, IndexActivity.class));
        finish();
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close_left, 0);
    }

    @Override
    public void onBackPressed() {
        backOff();
    }

    @Override
    protected void onPause() {
        robot = robotsView.getRobot();
        if (robot != null) {
            spt.putString(SptConfig.ROBOT_KEY, "" + robotsView.getRobot().getId());
            session.remove("index_refresh");
        }
        super.onPause();
    }

    public void showMessage(String msg) {
        showMsg("系统提示", "" + msg);
    }
}
