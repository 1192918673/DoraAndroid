package coms.geeknewbee.doraemon.box.smart_home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.smart_home.bean.SmartBean;
import coms.geeknewbee.doraemon.box.smart_home.presenter.ISmartPresenter;
import coms.geeknewbee.doraemon.box.smart_home.util.BLLocalDevice;
import coms.geeknewbee.doraemon.box.smart_home.util.BLLocalDeviceCode;
import coms.geeknewbee.doraemon.box.smart_home.util.BLLocalProbeListResponse;
import coms.geeknewbee.doraemon.box.smart_home.util.BLLocalResponse;
import coms.geeknewbee.doraemon.box.smart_home.util.BLM;
import coms.geeknewbee.doraemon.box.smart_home.view.ISmartView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class SmartHomeActivity extends BaseActivity implements ISmartView {

    ImageButton ibBack;

    LinearLayout llTv;

    LinearLayout llPurifier;

    LinearLayout llPlug;

    String robotPk;

    /**
     * 0，智能电视，1，智能插座
     */
    int type = 0;

    List<SmartBean> smartBeens;

    ISmartPresenter presenter;

    /**
     * -------------------临时数据--------------------
     **/

    int state = 0;

    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home);

        initViews();

        robotPk = getIntent().getStringExtra("robotPk");

        presenter = new ISmartPresenter(this);
        presenter.getInstructions();
    }

    public void initViews() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        llTv = (LinearLayout) findViewById(R.id.llTv);
        llPurifier = (LinearLayout) findViewById(R.id.llPurifier);
        llPlug = (LinearLayout) findViewById(R.id.llPlug);

        ibBack.setOnClickListener(clickListener);
        llTv.setOnClickListener(clickListener);
        llPurifier.setOnClickListener(clickListener);
        llPlug.setOnClickListener(clickListener);
    }

    /**
     * 返回到IndexActivity
     */
    public void backOff() {
        startActivity(new Intent(this, IndexActivity.class));
        finish();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibBack:
                    backOff();
                    break;

                case R.id.llTv:
                    type = 0;
                    if (!BLM.isInitSuccess()) {
                        showDialog("正在初始化设备……");
                        BLM.broadLinkInit(handler, getApplicationContext());
                    } else if (BLM.isSetWifi(spt)) {
                        Intent intentTv = new Intent(SmartHomeActivity.this, SmartTVStudyActivity.class);
                        intentTv.putExtra("robotPk", robotPk);
                        startActivity(intentTv);
                    } else {
                        Intent intentTv = new Intent(SmartHomeActivity.this, SmartTVActivity.class);
                        intentTv.putExtra("robotPk", robotPk);
                        startActivity(intentTv);
                    }
                    break;

                case R.id.llPurifier:
                    if (spt.getBoolean("jinghuaqi" + robotPk, false)) {
                        Intent intentPurifier = new Intent(SmartHomeActivity.this, SmartPurifierSuccessActivity.class);
                        intentPurifier.putExtra("type", 1);
                        intentPurifier.putExtra("robotPk", robotPk);
                        startActivity(intentPurifier);
                    } else {
                        Intent intentPurifier = new Intent(SmartHomeActivity.this, SmartPurifierActivity.class);
                        intentPurifier.putExtra("robotPk", robotPk);
                        startActivity(intentPurifier);
                    }
                    break;

                case R.id.llPlug:
                    type = 1;
                    if (!BLM.isInitSuccess()) {
                        showDialog("正在初始化设备……");
                        BLM.broadLinkInit(handler, getApplicationContext());
                    } else if (BLM.isSetSpWifi(spt)) {
                        Intent intentPlug = new Intent(SmartHomeActivity.this, SmartPurifierSuccessActivity.class);
                        intentPlug.putExtra("type", 2);
                        intentPlug.putExtra("robotPk", robotPk);
                        startActivity(intentPlug);
                    } else {
                        Intent intentPlug = new Intent(SmartHomeActivity.this, SmartPlugActivity.class);
                        intentPlug.putExtra("robotPk", robotPk);
                        startActivity(intentPlug);
                    }
//                    state++;
//                    if(state % 2 == 0){
//                        BLM.modifyPlugbase(handler, 0);
//                        tt.showMessage("关闭插座", tt.SHORT);
//                    } else {
//                        BLM.modifyPlugbase(handler, 1);
//                        tt.showMessage("打开插座", tt.SHORT);
//                    }
                    break;
            }
        }
    };

    /**
     * Broadlink接口调用的结果处理
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BLLocalResponse response = new Gson().fromJson((String) msg.obj, BLLocalResponse.class);
            switch (msg.what) {
                case BLM.MSG_WHAT_BL_INIT:
                    if (response.getCode() == 0 || response.getCode() == 3) {
                        if (response.getCode() == 3) {
                            // 已经联网
                            hideDialog();
                            Intent intentTv = new Intent();
                            if (type == 0) {
                                intentTv.setClass(SmartHomeActivity.this, SmartTVStudyActivity.class);
                            } else if (type == 1) {
                                intentTv.setClass(SmartHomeActivity.this, SmartPurifierSuccessActivity.class);
                                intentTv.putExtra("type", 2);
                            }
                            intentTv.putExtra("robotPk", robotPk);
                            startActivity(intentTv);
                            finish();
                        } else {
                            hideDialog();
                            if (BLM.isSetWifi(spt)) {
                                Intent intentTv = new Intent();
                                if (type == 0) {
                                    intentTv.setClass(SmartHomeActivity.this, SmartTVStudyActivity.class);
                                } else if (type == 1) {
                                    intentTv.setClass(SmartHomeActivity.this, SmartPurifierSuccessActivity.class);
                                    intentTv.putExtra("type", 2);
                                }
                                intentTv.putExtra("robotPk", robotPk);
                                startActivity(intentTv);
                            } else {
                                Intent intentTv = new Intent();
                                if (type == 0) {
                                    intentTv.setClass(SmartHomeActivity.this, SmartTVActivity.class);
                                } else if (type == 1) {
                                    intentTv.setClass(SmartHomeActivity.this, SmartPlugActivity.class);
                                }
                                intentTv.putExtra("robotPk", robotPk);
                                startActivity(intentTv);
                            }
                            finish();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public String getRobotPk() {
        return robotPk;
    }

    @Override
    public int getPage() {
        return 0;
    }

    @Override
    public void addSuccess() {

    }

    @Override
    public int getBaseId() {
        return 0;
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    public void setSmartBeans(List<SmartBean> smartBeens) {
        this.smartBeens = smartBeens;
        if (smartBeens != null && smartBeens.size() > 0) {
            int len = smartBeens.size();
            for (int i = 0; i < len; i++) {
                if ("0".equals(smartBeens.get(i).getType())) {
                    spt.putBoolean("jinghuaqi" + robotPk, true);
                } else if ("1".equals(smartBeens.get(i).getType())
                        || "3".equals(smartBeens.get(i).getType())
                        || "4".equals(smartBeens.get(i).getType())) {
                    spt.putBoolean("blIsSetWifi", true);
                    spt.putString("blMac", smartBeens.get(i).getData().split(",")[0]);
                } else if ("2".equals(smartBeens.get(i).getType())) {
                    spt.putBoolean("blIsSetSpWifi", true);
                    spt.putString("spMac", smartBeens.get(i).getData());
                    mac = smartBeens.get(i).getData();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        backOff();
    }

    @Override
    public void showMessage(String msg) {
        showMsg("系统提示", msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        BLM.cancel();
        super.onDestroy();
    }
}
