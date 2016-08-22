package coms.geeknewbee.doraemon.box.smart_home;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.smart_home.bean.SmartBean;
import coms.geeknewbee.doraemon.box.smart_home.presenter.ISmartPresenter;
import coms.geeknewbee.doraemon.box.smart_home.util.BLLocalResponse;
import coms.geeknewbee.doraemon.box.smart_home.util.BLM;
import coms.geeknewbee.doraemon.box.smart_home.view.ISmartView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.utils.NetworkStateReceiver;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class SmartPlugWifiActivity extends BaseActivity implements ISmartView {

    /**-----------------------组件----------------------**/

    EditText wifi_ssid;

    EditText wifi_password;

    Button wifi_ok;

    ImageButton ib_back;

    /**-----------------------数据----------------------**/

    String SSID;

    String GATE;

    int count = -1;

    String robotPk;

    int plug_id;

    ISmartPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_plug_wifi);
        assignViews();

        if (!NetworkStateReceiver.isNetworkAvailable(this)) {
            tt.showMessage("请先配置好您手机的WIFI网络", tt.LONG);
            finish();
        } else {
            wifiInit();
        }
    }

    private void assignViews() {
        wifi_ssid = (EditText) findViewById(R.id.wifi_ssid);
        wifi_password = (EditText) findViewById(R.id.wifi_password);
        wifi_ok = (Button) findViewById(R.id.wifi_ok);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        skm = new SoftKeyboardManager(wifi_ssid);

        robotPk = getIntent().getStringExtra("robotPk");

        wifi_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd = wifi_password.getText().toString();
                if(pwd.length() == 0){
                    showMsg("系统提示", "请输入WIFI密码。");
                    return;
                }
                showDialog("正在配置网络……");
                count = 0;
                handler.postDelayed(update, 1000);
                BLM.broadLinkEasyconfig(handler, SSID, pwd, GATE, BLM.SP);
            }
        });

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wifi_password.setOnFocusChangeListener(focusChangeListener);

        presenter = new ISmartPresenter(this);
        presenter.getSmarts();
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };

    private void wifiInit() {
        if (!NetworkStateReceiver.isConnected) {
            tt.showMessage("请先配置好您手机的WIFI网络", tt.LONG);
            finish();
        }
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ILog.e("wifiInfo", wifiInfo.toString());
        ILog.e("SSID", wifiInfo.getSSID());
        ILog.e("IP", long2ip(wifiInfo.getIpAddress()));
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        ILog.e("GATE", long2ip(dhcp.gateway));
        ILog.e("MASK", long2ip(dhcp.netmask));

        SSID = wifiInfo.getSSID().replaceAll("\\\"", "");
        GATE = long2ip(dhcp.gateway);
        wifi_ssid.setText(""+SSID);
    }

    String long2ip(long ip){
        StringBuffer sb=new StringBuffer();
        sb.append(String.valueOf((int)(ip&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>8)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>16)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>24)&0xff)));
        return sb.toString();
    }

    Runnable update = new Runnable() {
        @Override
        public void run() {
            if(count >= 0 && count < 100){
                // 3 + 2 + 2 + 1 + 1 + 1
                if(count < 30){
                    count += 3;
                } else if(count < 70){
                    count += 2;
                } else {
                    count += 1;
                }
                handler.postDelayed(update, 1000);
                showDialog("正在配置网络(" + count + "%)……");
            }
        }
    };

    @Override
    public void onBackPressed() {
        hideDialog();
        finish();
        super.onBackPressed();
    }

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
        return 1;
    }

    @Override
    public void addSuccess() {
        hideDialog();
        Intent intent = new Intent(SmartPlugWifiActivity.this, SmartPurifierSuccessActivity.class);
        intent.putExtra("robotPk", robotPk);
        intent.putExtra("type", 2);
        startActivity(intent);
        finish();
    }

    @Override
    public int getBaseId() {
        return plug_id;
    }

    @Override
    public String getData() {
        return BLM.getSpMacAddress();
    }

    @Override
    public void setSmartBeans(List<SmartBean> smartBeens) {
        if(smartBeens != null && smartBeens.size() > 0){
            int len = smartBeens.size();
            for (int i = 0; i < len; i++){
                if("2".equals(smartBeens.get(i).getCategory())
                        && smartBeens.get(i).getWord().contains("插座")){
                    plug_id = smartBeens.get(i).getId();
                    break;
                }
            }
        }
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

    /**
     * Broadlink接口调用的结果处理
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BLLocalResponse response = new Gson().fromJson((String) msg.obj, BLLocalResponse.class);
            switch (msg.what) {
                case BLM.MSG_WHAT_BL_EASYCONFIG:
                    if (response.getCode() == 0) {
                        BLM.broadLinkProbeList(handler);
                    } else {
                        count = -1;
                        ILog.e("BroadLink EASYCONFIG失败：" + response.getMsg());
                        hideDialog();
                        showMsg("系统提示", "网络配置失败：" + response.getMsg());
                    }
                    break;
                case BLM.MSG_WHAT_BL_PROBELIST:

                    break;
                case BLM.MSG_WHAT_BL_SCANSTUDY:
                    if (response.getCode() == 0) {
                        ILog.e("BroadLink SCANSTUDY返回成功：" + response.getMsg());
                    }
                    break;
                case BLM.MSG_WHAT_BL_DEVICEADD:
                    if (response.getCode() == 0) {
                        count = -1;
                        ILog.e("BroadLink DEVICEADD返回成功：" + response.getMsg());
                        ILog.e("BroadLink设备准备就绪");
                        presenter.addInstructions();
                    }
                    break;
            }
        }
    };
}
