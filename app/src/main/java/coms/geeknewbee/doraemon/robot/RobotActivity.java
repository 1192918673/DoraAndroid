package coms.geeknewbee.doraemon.robot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.widget.RobotsView;

/**
 * Created by chen on 2016/4/6
 */
public class RobotActivity extends BaseActivity {

    /**---------------------组件--------------------**/
    private ImageButton ibBack;

    RobotsView robotsView;

    private TextView tvAdd;

    private RelativeLayout act_msg;

    private RelativeLayout act_robot;

    private RelativeLayout act_member;

    private RelativeLayout actOffline;

    private RelativeLayout actVoice;

    /**---------------------数据--------------------**/
    RobotBean robot;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvAdd = (TextView) findViewById(R.id.tv_add);
        act_msg = (RelativeLayout) findViewById(R.id.act_msg);
        act_robot = (RelativeLayout) findViewById(R.id.act_robot);
        act_member = (RelativeLayout) findViewById(R.id.act_member);
        robotsView = (RobotsView)findViewById(R.id.robotsView);
        actOffline = (RelativeLayout) findViewById(R.id.actOffline);
        actVoice = (RelativeLayout) findViewById(R.id.actVoice);
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
        if(session.contains("robotBeans")){
            List<RobotBean> robotBeans =
                    (List<RobotBean>)session.get("robotBeans");
            setData(robotBeans);
        } else {
            setData(null);
        }
    }

    public void setData(List<RobotBean> robotBeans) {
        robotsView.setRobots(robotBeans);
        robotsView.setRobotKey(spt.getString(SptConfig.ROBOT_KEY, null));
        if(robotBeans == null || robotBeans.size() == 0){
            act_msg.setOnClickListener(null);
            act_robot.setOnClickListener(null);
            act_member.setOnClickListener(null);
            actOffline.setOnClickListener(null);
        } else {
            robot = robotsView.getRobot();
            act_msg.setOnClickListener(clickListener);
            act_robot.setOnClickListener(clickListener);
            act_member.setOnClickListener(clickListener);
            actVoice.setOnClickListener(clickListener);
            actOffline.setOnClickListener(clickListener);
        }
    }

    private void initListener() {
        ibBack.setOnClickListener(clickListener);
        tvAdd.setOnClickListener(clickListener);
//        actOffline.setOnClickListener(clickListener);
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_back:
                    finish();
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
                    Intent intentOffline = new Intent(RobotActivity.this, RobotOfflineActivity.class);
                    startActivity(intentOffline);
                    break;
                case R.id.actVoice:
                    Intent intent_voice = new Intent(RobotActivity.this, RobotVoiceActivity.class);
                    intent_voice.putExtra("robotPk", "" + robotsView.getRobot().getId());
                    startActivity(intent_voice);
                    break;
            }
        }
    };

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close_left, 0);
    }

    @Override
    protected void onPause() {
        robot = robotsView.getRobot();
        if(robot != null){
            spt.putString(SptConfig.ROBOT_KEY, "" + robotsView.getRobot().getId());
            session.remove("index_refresh");
        }
        super.onPause();
    }

    public void showMessage(String msg) {
        showMsg("系统提示", "" + msg);
    }
}
