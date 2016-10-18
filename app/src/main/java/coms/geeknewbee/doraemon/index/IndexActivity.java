package coms.geeknewbee.doraemon.index;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.BoxActivity;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.CenterActivity;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.index.presenter.IRobotsLoadPresenter;
import coms.geeknewbee.doraemon.index.view.IIndexView;
import coms.geeknewbee.doraemon.robot.RobotActivity;
import coms.geeknewbee.doraemon.robot.RobotWifiActivity;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.widget.CircleView;

/**
 * 首页
 */
public class IndexActivity extends BaseActivity implements IIndexView, View.OnClickListener {

    /**------------------- 控制组件 ------------------**/
    /**
     * 百宝箱
     **/
    private Button bt_Menu;
    /**
     * 用户中心
     **/
    private ImageButton ibCenter;
    /**
     * 设置
     **/
    private ImageButton ibSettings;
    /**
     * 根布局
     **/
    private RelativeLayout mRl;

    /**------------------- 机器人相关组件 ------------------**/
    /**
     * 机器人名字
     **/
    TextView dr_name;

    /**
     * 表盘容器
     **/
    RelativeLayout dr_panel;

    /** 安防模式 **/
    //CircleView dr_safe;

    /**
     * 电量
     **/
    CircleView dr_electric;

    /**
     * 音量
     **/
    CircleView dr_vol;

    /**
     * 机器人图标
     **/
    ImageView dora;

    /**
     * 机器人说话
     **/
    TextView dr_talk;

    /**
     * 添加机器人
     **/
    LinearLayout dr_add;

    /**
     * ------------------- 数据 ------------------
     **/
    public static IndexActivity instance = null;

    IRobotsLoadPresenter loadPresenter;

    RobotBean robot;

    @Override
    public void onBackPressed() {
        // 两次返回键退出
        if (tt.isDoubleClick("再按一次退出应用")) {
            session.closeAllActivities();
            finish();
        }

        ILog.e(spt.getString(SptConfig.LOGIN_TOKEN, "-------------"));
        ILog.e(session.CLIENT_ID + " : " + session.get(session.CLIENT_ID));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        instance = this;
        initView();
    }

    private void initView() {
        ibSettings = (ImageButton) findViewById(R.id.ib_settings);
        ibCenter = (ImageButton) findViewById(R.id.ib_center);
        bt_Menu = (Button) findViewById(R.id.bt_menu);
        mRl = (RelativeLayout) findViewById(R.id.rl);
        dr_name = (TextView) findViewById(R.id.dr_name);

        //dr_safe = (CircleView) findViewById(R.id.dr_safe);
        dr_vol = (CircleView) findViewById(R.id.dr_vol);
        dr_electric = (CircleView) findViewById(R.id.dr_electric);
        dr_panel = (RelativeLayout) findViewById(R.id.dr_panel);
        dora = (ImageView) findViewById(R.id.dora);

        dr_talk = (TextView) findViewById(R.id.dr_talk);
        dr_add = (LinearLayout) findViewById(R.id.dr_add);

        loadPresenter = new IRobotsLoadPresenter(this);

        //listener
        bt_Menu.setOnClickListener(this);
        ibCenter.setOnClickListener(this);
        ibSettings.setOnClickListener(this);
        dr_add.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.put(PushReceiver.ACTION_BATTERY_CHANGED, onBatteryChanged);
        if (session.contains("robotBeans")) {
            List<RobotBean> robotBeans =
                    (List<RobotBean>) session.get("robotBeans");
            setData(robotBeans);
        } else if (!hasDialog()) {
            showDialog("正在刷新数据……");
            loadPresenter.getRobot();
        }
        if (!session.contains(Session.USER) || session.get(Session.USER) == null) {
            loadPresenter.getUser();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_menu:
                if (session.contains("robotBeans")) {
                    List<RobotBean> robotBeans = (List<RobotBean>) session.get("robotBeans");
                    if (robotBeans == null || robotBeans.size() == 0) {
                        showMessage("你还没有可以管理的设备。");
                    } else {
                        Intent intentBox = new Intent(IndexActivity.this, BoxActivity.class);
                        intentBox.putExtra("robotPk", "" + robot.getId());
                        startActivity(intentBox);
                        overridePendingTransition(R.anim.activity_open, 0);
                    }
                } else {
                    showMessage("你还没有可以管理的设备。");
                }
                break;
            case R.id.ib_center:
                //个人中心页面
                startActivity(new Intent(IndexActivity.this, CenterActivity.class));
                this.overridePendingTransition(R.anim.activity_open_left, 0);
                break;
            case R.id.ib_settings:
                //设备管理页面
                startActivity(new Intent(IndexActivity.this, RobotActivity.class));
                this.overridePendingTransition(R.anim.activity_open_left, 0);
                break;
            case R.id.dr_add:
                // 添加设备
                startActivity(new Intent(IndexActivity.this, RobotWifiActivity.class));
                this.overridePendingTransition(R.anim.activity_open_left, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public void setData(List<RobotBean> robotBeans) {
        hideDialog();
        session.put("index_refresh", false);
        session.put("robotBeans", robotBeans);
        if (robotBeans == null || robotBeans.size() == 0) {
            // 没有数据
            dr_name.setText("--");
            /** 表盘容器 **/
            dr_panel.setVisibility(View.INVISIBLE);
            /** 机器人图标 **/
            dora.setVisibility(View.INVISIBLE);
            /** 机器人说话 **/
            dr_talk.setVisibility(View.INVISIBLE);
            /** 添加机器人 **/
            dr_add.setVisibility(View.VISIBLE);
        } else {
            robot = robotBeans.get(0);
            String pk = spt.getString(SptConfig.ROBOT_KEY, null);
            if (pk != null) {
                int len = robotBeans.size();
                for (int i = 0; i < len; i++) {
                    if (pk.equals(robotBeans.get(i).getId() + "")) {
                        robot = robotBeans.get(i);
                        break;
                    }
                }
            }
            dr_name.setText("" + robot.getName());
            /** 表盘容器 **/
            dr_panel.setVisibility(View.VISIBLE);
            /** 机器人图标 **/
            dora.setVisibility(View.VISIBLE);
            /** 机器人说话 **/
            //dr_talk.setVisibility(View.VISIBLE);
            /** 添加机器人 **/
            dr_add.setVisibility(View.GONE);
            //dr_safe.setInfo("安防模式");
            //dr_safe.setNum(-2);
            dr_electric.setInfo("电量");
            dr_electric.setNum(robot.getBattery());
            dr_vol.setInfo("音量");
            dr_vol.setNum(robot.getVolume());
            // dr_talk.setText("");
        }
    }

    @Override
    public void showMessage(String msg) {
        session.put("index_refresh", false);
        hideDialog();
        showMsg("系统提示", msg);
        // 按照没有数据处理
        dr_name.setText("--");
        /** 表盘容器 **/
        dr_panel.setVisibility(View.INVISIBLE);
        /** 机器人图标 **/
        dora.setVisibility(View.INVISIBLE);
        /** 机器人说话 **/
        //dr_talk.setVisibility(View.INVISIBLE);
        /** 添加机器人 **/
        dr_add.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        session.remove(PushReceiver.ACTION_BATTERY_CHANGED);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }

    @Override
    public void setData(UserBean user) {
        session.put(Session.USER, user);
    }

    PushReceiver.OnBatteryChanged onBatteryChanged = new PushReceiver.OnBatteryChanged() {
        @Override
        public void onBatteryChanged(int percent) {
            if (dr_electric != null) {
                dr_electric.setNum(percent);
            }
        }
    };
}
