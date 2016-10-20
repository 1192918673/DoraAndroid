package coms.geeknewbee.doraemon.robot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotManagerPresenter;
import coms.geeknewbee.doraemon.robot.view.IManagerView;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class RobotManagerActivity extends BaseActivity implements IManagerView {

    /**
     * -----------------------组件----------------------
     **/

    EditText dr_name;

    EditText dr_wifi;

    TextView tv_wifi_set;

    Button dr_hard;

    Button dr_remove;

    Button ok;

    SeekBar dr_vol;

    ImageButton ib_back;

    /**
     * -----------------------数据----------------------
     **/

    RobotBean robot;

    IRobotManagerPresenter presenter;

    String robotPk;

    UserBean user;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_manager);
        assignViews();

        robotPk = getIntent().getStringExtra("robotPk");
    }

    private void assignViews() {
        dr_name = (EditText) findViewById(R.id.dr_name);
        dr_wifi = (EditText) findViewById(R.id.dr_wifi);
        dr_hard = (Button) findViewById(R.id.dr_hard);
        dr_remove = (Button) findViewById(R.id.dr_remove);
        ok = (Button) findViewById(R.id.ok);
        tv_wifi_set = (TextView) findViewById(R.id.tv_wifi_set);
        dr_vol = (SeekBar) findViewById(R.id.dr_vol);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        skm = new SoftKeyboardManager(dr_name);

        presenter = new IRobotManagerPresenter(this);
        dr_vol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.modifyRobot();
                sendVolume();
            }
        });
        ib_back.setOnClickListener(clickListener);
        dr_hard.setOnClickListener(clickListener);
        dr_remove.setOnClickListener(clickListener);
        tv_wifi_set.setOnClickListener(clickListener);
        ok.setOnClickListener(clickListener);

        if (session.contains(Session.USER)) {
            user = (UserBean) session.get(Session.USER);
            login();
        }
        dr_name.setOnFocusChangeListener(focusChangeListener);
        dr_wifi.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                skm.show();
            }
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_back:
                    finish();
                    break;

                case R.id.dr_hard:
                    Intent intent_hard = new Intent(RobotManagerActivity.this, RobotHardInfoActivity.class);
                    intent_hard.putExtra("robotKey", "" + robot.getId());
                    startActivity(intent_hard);
                    break;

                case R.id.dr_remove:
                    presenter.removeRobot();
                    break;

                case R.id.tv_wifi_set:
                    Intent intent = new Intent(RobotManagerActivity.this, RobotWifiActivity.class);
                    intent.putExtra("type", "reLink");
                    startActivityForResult(intent, 0);
                    break;

                case R.id.ok:
                    name = dr_name.getText().toString().trim();
                    name = name.replace(" ", "");
                    name = name.replace("\n", "");
                    dr_name.setText(name);
                    presenter.modifyRobot();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (session.contains("robotBeans")) {
            List<RobotBean> robotBeans =
                    (List<RobotBean>) session.get("robotBeans");
            if (robotBeans == null || robotBeans.size() == 0) {
                tt.showMessage("该机器人已不存在！", tt.SHORT);
                finish();
            } else {
                int len = robotBeans.size();
                for (int i = 0; i < len; i++) {
                    if (("" + robotBeans.get(i).getId()).equals(robotPk)) {
                        robot = robotBeans.get(i);
                        dr_name.setText("" + robot.getName());
                        dr_wifi.setText("" + robot.getSsid());
                        dr_vol.setProgress(robot.getVolume());
                        break;
                    }
                }
                if (robot == null) {
                    tt.showMessage("该机器人已不存在！", tt.SHORT);
                    finish();
                }
            }
        } else {
            tt.showMessage("该机器人已不存在！", tt.SHORT);
            finish();
        }
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public String getRobotPk() {
        return "" + robot.getId();
    }

    @Override
    public String getName() {
        name = dr_name.getText().toString().trim();
        name = name.replace(" ", "");
        name = name.replace("\n", "");
        return name;
    }

    @Override
    public int getVolume() {
        return dr_vol.getProgress();
    }

    @Override
    public void setRobotBean(RobotBean robot) {
        if (StringHandler.isEmpty(presenter.getName())) {
            hideDialog();
            session.remove("index_refresh");
            List<RobotBean> robotBeans =
                    (List<RobotBean>) session.get("robotBeans");
            for (RobotBean robotBean : robotBeans) {
                if (robotPk.equals("" + robotBean.getId())) {
                    robotBeans.remove(robotBean);
                    break;
                }
            }
            if (robotBeans.size() > 0) {
                spt.putString(SptConfig.ROBOT_KEY, "" + robotBeans.get(0).getId());
            }
            finish();
        }
        this.robot.setName(presenter.getName());
        this.robot.setVolume(presenter.getVol());
        session.remove("index_refresh");
    }

    @Override
    public RobotBean getRobot() {
        return robot;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void showMessage(String msg) {
        showMsg("系统提示", msg);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String ssid = data.getStringExtra("ssid");
            if (!StringHandler.isEmpty(ssid)) {
                this.robot.setSsid(ssid);
                dr_wifi.setText("" + ssid);
            }
        }
    }

    public void login() {
        if (!session.contains("EMClient.login")) {
            EMClient.getInstance().login(user.getHx_user().getUsername(),
                    user.getHx_user().getPassword(), new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    session.put("EMClient.login", true);
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }

                        @Override
                        public void onError(int code, String message) {
                            asyncToast("连接服务器失败");
                        }
                    });
        }
    }

    public void sendVolume() {
        if (session.contains("robotBeans")) {
            List<RobotBean> robotBeans =
                    (List<RobotBean>) session.get("robotBeans");
            RobotBean robot = robotBeans.get(0);
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
            EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
            cmdMsg.setReceipt(robot.getHx_username());
            cmdMsg.addBody(new EMCmdMessageBody("{\"data\":" + getVolume() + ", \"type\":2}"));
            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
        }
    }
}
