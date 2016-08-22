package coms.geeknewbee.doraemon.box.sendvoice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import butterknife.ButterKnife;
import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.sendvoice.presenter.SendVoicePresenter;
import coms.geeknewbee.doraemon.box.sendvoice.view.ISendVoiceView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.widget.CircleButtonView;
import coms.geeknewbee.doraemon.widget.CircleView;

/**
 * Created by chen on 2016/3/30
 */
public class SendVoiceActivity extends BaseActivity implements ISendVoiceView {

    private ImageButton ibGoback;
    private TextView tvTitle;
    private EditText etTxt;
    private TextView tvRest;
    private CircleButtonView btnSend;

    String robotPk;

    UserBean user;

    private void assignViews() {
        ibGoback = (ImageButton) findViewById(R.id.ib_goback);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        etTxt = (EditText) findViewById(R.id.et_txt);
        tvRest = (TextView) findViewById(R.id.tv_rest);
        btnSend = (CircleButtonView) findViewById(R.id.btn_send);
        btnSend.setText("发送");

        skm = new SoftKeyboardManager(etTxt);
        robotPk = getIntent().getStringExtra("robotPk");
    }

    private int num = 30;

    SendVoicePresenter presenter = new SendVoicePresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);
        assignViews();
        initListener();
    }

    /**
     * 监听
     */
    private void initListener() {
        //返回键
        ibGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOff();
            }
        });
        //发送键
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnSend.isLoading()){
                    return;
                }
                if(getText().length() == 0){
                    showMsg("系统提示", "发送内容不能为空。");
                    return;
                }
                // presenter.sendText();
                // btnSend.startLoading();
                sendText();
            }
        });
        //剩余可输入控制
        etTxt.addTextChangedListener(textWatcher);

        if(session.contains(Session.USER)){
            user = (UserBean) session.get(Session.USER);
            login();
        }
        etTxt.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };

    /**
     * TextWatcher:接口。继承它要实现其三个方法，分别在EditText改变前，改变过程中，改变之后各自发生的动作
     */
    TextWatcher textWatcher = new TextWatcher() {
        private CharSequence temp;
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //将每次输入都记录
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            //获取已输入长度
            int number = num - s.length();
            tvRest.setText("还可以输入" + number + "字");
            selectionStart = etTxt.getSelectionStart();
            selectionEnd = etTxt.getSelectionEnd();
            if (temp.length() > num) {
                s.delete(selectionStart - 1, selectionEnd);
                int tempSelection = selectionStart;
                etTxt.setText(s);
                etTxt.setSelection(tempSelection);//将光标设置最后
                tvRest.setTextColor(Color.RED);
            } else {
                tvRest.setTextColor(Color.parseColor("#9FA0A0"));
            }
        }
    };

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }


    /**
     * 返回到IndexActivity
     */
    @Override
    public void backOff() {
        startActivity(new Intent(SendVoiceActivity.this, IndexActivity.class));
        finish();
    }

    /**
     * 获取文字输入
     * @return
     */
    @Override
    public String getText() {
        return etTxt.getText().toString().trim();
    }

    /**
     * Clear
     */
    @Override
    public void clear() {
        etTxt.setText("");
    }

    @Override
    public String getRobotPk() {
        return robotPk;
    }

    /**
     * 从Sp中获取token
     * @return
     */
    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, "token-null");
    }

    /**
     * 显示成功,友好提示
     */
    @Override
    public void showSuccess() {
        clear();
        btnSend.stopLoading();
        Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示失败,友好提示
     */
    @Override
    public void showFailed(String error) {
        btnSend.stopLoading();
        showMsg("系统提示", "发送失败:" + error);
    }

    public void login(){
        if(!session.contains("EMClient.login")){
            EMClient.getInstance().login(user.getHx_user().getUsername(),
                    user.getHx_user().getPassword(), new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            ILog.e("环信登录成功");
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
                            ILog.e("环信登录失败：" + message);
                            asyncToast("连接服务器失败");
                        }
                    });
        }
    }

    public void sendText(){
        if(session.contains("robotBeans")){
            List<RobotBean> robotBeans =
                    (List<RobotBean>)session.get("robotBeans");
            RobotBean robot = robotBeans.get(0);
            String pk = spt.getString(SptConfig.ROBOT_KEY, null);
            if(pk != null){
                int len = robotBeans.size();
                for (int i = 0; i < len; i++){
                    if(pk.equals(robotBeans.get(i).getId() + "")){
                        robot = robotBeans.get(i);
                        break;
                    }
                }
            }
            EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
            cmdMsg.setReceipt(robot.getHx_username());
            cmdMsg.addBody(new EMCmdMessageBody("{\"data\":\"" + getText() + "\", \"type\":1}"));
            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
            clear();
            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(String msg) {
        showMsg("系统提示", "" + msg);
    }

    @Override
    protected void onDestroy() {
        btnSend.stopLoading();
        super.onDestroy();
    }
}
