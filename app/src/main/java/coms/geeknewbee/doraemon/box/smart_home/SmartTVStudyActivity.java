package coms.geeknewbee.doraemon.box.smart_home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.smart_home.bean.SmartBean;
import coms.geeknewbee.doraemon.box.smart_home.presenter.ISmartPresenter;
import coms.geeknewbee.doraemon.box.smart_home.util.BLLocalDeviceCode;
import coms.geeknewbee.doraemon.box.smart_home.util.BLLocalResponse;
import coms.geeknewbee.doraemon.box.smart_home.util.BLM;
import coms.geeknewbee.doraemon.box.smart_home.view.ISmartView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class SmartTVStudyActivity extends BaseActivity implements ISmartView {

    ImageButton ibBack;

    RelativeLayout rlClose;

    RelativeLayout rlOn;

    RelativeLayout rlOff;

    RelativeLayout rlCurtainOpen;

    RelativeLayout rlCurtainClose;

    RelativeLayout rlCurtainOff;

    RelativeLayout rlTouch01;

    RelativeLayout rlTouch02;

    RelativeLayout rlTouch03;

    LinearLayout llNew;

    RelativeLayout llStudy;

    ImageView ivClose;

    TextView tvCancel;

    Button bSetWifi;

    TextView tvInfo01;

    TextView tvInfo02;

    TextView tvInfo03;

    TextView tvCurtainOpen;

    TextView tvCurtainClose;

    TextView tvCurtainOff;

    TextView tvTouch01;

    TextView tvTouch02;

    TextView tvTouch03;

    String robotPk;

    int index = 0;

    String code;

    List<SmartBean> smartBeens;

    ISmartPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_tv_study);

        initViews();
    }

    public void initViews(){

        ibBack = (ImageButton)findViewById(R.id.ibBack);
        rlClose = (RelativeLayout)findViewById(R.id.rlClose);
        rlOn = (RelativeLayout)findViewById(R.id.rlOn);
        rlOff = (RelativeLayout)findViewById(R.id.rlOff);
        bSetWifi = (Button)findViewById(R.id.bSetWifi);

        tvInfo01 = (TextView)findViewById(R.id.tvInfo01);
        tvInfo02 = (TextView)findViewById(R.id.tvInfo02);
        tvInfo03 = (TextView)findViewById(R.id.tvInfo03);
        llNew = (LinearLayout)findViewById(R.id.llNew);
        llStudy = (RelativeLayout)findViewById(R.id.llStudy);

        ivClose = (ImageView)findViewById(R.id.ivClose);
        tvCancel = (TextView)findViewById(R.id.tvCancel);
        rlCurtainOpen = (RelativeLayout)findViewById(R.id.rlCurtainOpen);
        rlCurtainClose = (RelativeLayout)findViewById(R.id.rlCurtainClose);
        rlCurtainOff = (RelativeLayout)findViewById(R.id.rlCurtainOff);

        rlTouch01 = (RelativeLayout)findViewById(R.id.rlTouch01);
        rlTouch02 = (RelativeLayout)findViewById(R.id.rlTouch02);
        rlTouch03 = (RelativeLayout)findViewById(R.id.rlTouch03);
        tvCurtainOpen = (TextView)findViewById(R.id.tvCurtainOpen);
        tvCurtainClose = (TextView)findViewById(R.id.tvCurtainClose);

        tvCurtainOff = (TextView)findViewById(R.id.tvCurtainOff);
        tvTouch01 = (TextView)findViewById(R.id.tvTouch01);
        tvTouch02 = (TextView)findViewById(R.id.tvTouch02);
        tvTouch03 = (TextView)findViewById(R.id.tvTouch03);

        robotPk = getIntent().getStringExtra("robotPk");
        presenter = new ISmartPresenter(this);

        ibBack.setOnClickListener(clickListener);
        rlClose.setOnClickListener(clickListener);
        rlOn.setOnClickListener(clickListener);
        rlOff.setOnClickListener(clickListener);
        bSetWifi.setOnClickListener(clickListener);
        tvCancel.setOnClickListener(clickListener);

        rlCurtainOpen.setOnClickListener(clickListener);
        rlCurtainClose.setOnClickListener(clickListener);
        rlCurtainOff.setOnClickListener(clickListener);
        rlTouch01.setOnClickListener(clickListener);
        rlTouch02.setOnClickListener(clickListener);
        rlTouch03.setOnClickListener(clickListener);

        presenter.getInstructions();

        if(spt.getBoolean("___llNew", true)){
            llNew.setVisibility(View.VISIBLE);
            ivClose.setOnClickListener(clickListener);
            spt.putBoolean("___llNew", false);
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ibBack:
                    finish();
                    break;

                case R.id.ivClose:
                    llNew.setVisibility(View.GONE);
                    break;

                case R.id.tvCancel:
                    llStudy.setVisibility(View.GONE);
                    BLM.cancel();
                    tt.showMessage("已经取消学习", tt.SHORT);
                    break;

                case R.id.rlClose:
                    index = 1;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlOn:
                    index = 2;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlOff:
                    index = 3;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlCurtainOpen:
                    index = 8;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlCurtainClose:
                    index = 9;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlCurtainOff:
                    index = 10;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlTouch01:
                    index = 11;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlTouch02:
                    index = 13;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.rlTouch03:
                    index = 15;
                    llStudy.setVisibility(View.VISIBLE);
                    handler.postDelayed(finish, 20000);
                    BLM.broadLinkRMProScanAndStudy(handler);
                    break;

                case R.id.bSetWifi:
                    //BLM.broadLinkRMProSend(handler, code);
                    Intent intent = new Intent(SmartTVStudyActivity.this, SmartTVActivity.class);
                    intent.putExtra("robotPk", robotPk);
                    startActivity(intent);
                    finish();
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
    public String getData() {
        return BLM.getMacAddress() + "," + code;
    }

    @Override
    public int getPage() {
        return 1;
    }

    @Override
    public int getBaseId() {
        if(smartBeens != null && smartBeens.size() > 0){
            int len = smartBeens.size();
            for (int i = 0; i < len; i++){
                String word = smartBeens.get(i).getWord();
                if(index == 1){
                    if(smartBeens.get(i).getCategory().equals("1")
                            && (word.contains("开") && word.contains("关"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 2){
                    if(smartBeens.get(i).getCategory().equals("1")
                            && (word.contains("提高") && word.contains("声音"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 3){
                    if(smartBeens.get(i).getCategory().equals("1")
                            && (word.contains("降低") && word.contains("声音"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 8){
                    if(smartBeens.get(i).getCategory().equals("3")
                            && (word.contains("窗帘") && word.contains("开"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 9){
                    if(smartBeens.get(i).getCategory().equals("3")
                            && (word.contains("窗帘") && word.contains("关"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 10){
                    if(smartBeens.get(i).getCategory().equals("3")
                            && (word.contains("窗帘") && word.contains("停止"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 11){
                    if(smartBeens.get(i).getCategory().equals("4")
                            && (word.contains("上"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 13){
                    if(smartBeens.get(i).getCategory().equals("4")
                            && (word.contains("下"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                } else if(index == 15){
                    if(smartBeens.get(i).getCategory().equals("4")
                            && (word.contains("右"))){
                        index = smartBeens.get(i).getId();
                        break;
                    }
                }
            }
        }
        return index;
    }

    @Override
    public void setSmartBeans(List<SmartBean> smartBeens) {
        if(smartBeens != null && smartBeens.size() > 0){
            if(StringHandler.isEmpty(smartBeens.get(0).getType())
                    || StringHandler.isEmpty(smartBeens.get(0).getData())){
                this.smartBeens = smartBeens;
            } else {
                int len = smartBeens.size();
                for (int i = 0; i < len; i++){
                    String word = smartBeens.get(i).getWord();
                    if(smartBeens.get(i).getType().equals("1")
                            && (word.contains("开") && word.contains("关"))){
                        //code = smartBeens.get(i).getData().split(",")[1];
                        tvInfo01.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("1")
                            && (word.contains("提高") && word.contains("声音"))){
                        tvInfo02.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("1")
                            && (word.contains("降低") && word.contains("声音"))){
                        tvInfo03.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("3")
                            && (word.contains("窗帘") && word.contains("开"))){
                        tvCurtainOpen.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("3")
                            && (word.contains("窗帘") && word.contains("关"))){
                        tvCurtainClose.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("3")
                            && (word.contains("窗帘") && word.contains("停止"))){
                        tvCurtainOff.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("4")
                            && (word.contains("上"))){
                        tvTouch01.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("4")
                            && (word.contains("下"))){
                        tvTouch02.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getType().equals("4")
                            && (word.contains("右"))){
                        tvTouch03.setVisibility(View.VISIBLE);
                    }
                }
                presenter.getSmarts();
            }
        }
    }

    @Override
    public void addSuccess() {
        llStudy.setVisibility(View.GONE);
        showMessage("学习成功！（如果控制电视失败，请重新学习。" +
                "提示：点击遥控器时，点的重一点比点的轻一点更有效。）");
        if(smartBeens != null && smartBeens.size() > 0){
            int len = smartBeens.size();
            for (int i = 0; i < len; i++){
                String word = smartBeens.get(i).getWord();
                if(index == smartBeens.get(i).getId()){
                    if(smartBeens.get(i).getCategory().equals("1")
                            && (word.contains("开") && word.contains("关"))){
                        tvInfo01.setVisibility(View.VISIBLE);
                        break;
                    } else if(smartBeens.get(i).getCategory().equals("1")
                            && (word.contains("提高") && word.contains("声音"))){
                        tvInfo02.setVisibility(View.VISIBLE);
                        break;
                    } else if(smartBeens.get(i).getCategory().equals("1")
                            && (word.contains("降低") && word.contains("声音"))){
                        tvInfo03.setVisibility(View.VISIBLE);
                        break;
                    } else if(smartBeens.get(i).getCategory().equals("3")
                            && (word.contains("窗帘") && word.contains("开"))){
                        tvCurtainOpen.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getCategory().equals("3")
                            && (word.contains("窗帘") && word.contains("关"))){
                        tvCurtainClose.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getCategory().equals("3")
                            && (word.contains("窗帘") && word.contains("停止"))){
                        tvCurtainOff.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getCategory().equals("4")
                            && (word.contains("上"))){
                        tvTouch01.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getCategory().equals("4")
                            && (word.contains("下"))){
                        tvTouch02.setVisibility(View.VISIBLE);
                    } else if(smartBeens.get(i).getCategory().equals("4")
                            && (word.contains("右"))){
                        tvTouch03.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            if(index == 1){
                tvInfo01.setVisibility(View.VISIBLE);
            } else if(index == 2){
                tvInfo02.setVisibility(View.VISIBLE);
            } else if(index == 3){
                tvInfo03.setVisibility(View.VISIBLE);
            } else if(index == 8){
                tvCurtainOpen.setVisibility(View.VISIBLE);
            } else if(index == 9){
                tvCurtainClose.setVisibility(View.VISIBLE);
            } else if(index == 10){
                tvCurtainOff.setVisibility(View.VISIBLE);
            } else if(index == 11){
                tvTouch01.setVisibility(View.VISIBLE);
            } else if(index == 13){
                tvTouch02.setVisibility(View.VISIBLE);
            } else if(index == 15){
                tvTouch03.setVisibility(View.VISIBLE);
            }
        }
    }

    Runnable finish = new Runnable() {
        @Override
        public void run() {
            if(hasDialog()){
                hideDialog();
                BLM.cancel();
                showMessage("学习超时。");
            }
        }
    };

    @Override
    public void onBackPressed() {
        if(llNew.getVisibility() == View.VISIBLE){
            llNew.setVisibility(View.GONE);
        } else if(llStudy.getVisibility() == View.VISIBLE){
            llStudy.setVisibility(View.GONE);
            BLM.cancel();
            tt.showMessage("已经取消学习", tt.SHORT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        BLM.cancel();
        super.onDestroy();
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
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
                case BLM.MSG_WHAT_BL_CODE:
                    if (response.getCode() == 0) {
                        BLLocalDeviceCode cmdData = new Gson().fromJson((String) msg.obj, BLLocalDeviceCode.class);
                        ILog.e("学习指令成功 : " + cmdData.getData().length() + " - " + cmdData.getData());
                        code = cmdData.getData();
                        if(code.length() > 2980){
                            code = code.substring(0, 2980);
                        }
                        handler.removeCallbacks(finish);
                        presenter.addInstructions();
                    }
                    break;
            }
        }
    };
}
