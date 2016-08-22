package coms.geeknewbee.doraemon.index.center;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.register_login.SmsReciver;
import coms.geeknewbee.doraemon.register_login.presenter.ResetPwdPresenter;
import coms.geeknewbee.doraemon.register_login.view.IResetPwdView;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/4/6
 */
public class UserUpdatePwd extends BaseActivity implements IResetPwdView {

    private ImageButton ibBack;
    private EditText etRegisterUsernameId;
    private EditText etRegisterCodeId;
    private TextView btGetcodeId;
    private Button btRegisterId;

    ResetPwdPresenter presenter = new ResetPwdPresenter(this);

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        etRegisterUsernameId = (EditText) findViewById(R.id.et_register_username_id);
        etRegisterCodeId = (EditText) findViewById(R.id.et_register_code_id);
        btGetcodeId = (TextView) findViewById(R.id.bt_getcode_id);
        btRegisterId = (Button) findViewById(R.id.bt_register_id);
        skm = new SoftKeyboardManager(etRegisterUsernameId);

        etRegisterUsernameId.setEnabled(false);
        UserBean user = (UserBean)session.get(session.USER);
        etRegisterUsernameId.setText("" + user.getMobile());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        assignViews();
        initListener();
    }

    public void initListener() {
        //返回按钮
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toUserLoginActivity();
            }
        });
        //获取验证码按钮点击事件
        btGetcodeId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckNull();
            }
        });
        //注册按钮点击事件
        btRegisterId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInvalidInput();
            }
        });
        SmsReciver.setOnSmsRecived(new SmsReciver.OnSmsRecived() {
            @Override
            public void sendCode(String code) {
                hideDialog();
                etRegisterCodeId.setText(code);
            }
        });
    }

    private void CheckNull() {
        if (!TextUtils.isEmpty(getMobile())) {
            if (!StringHandler.testPhone(getMobile())) {
                //手机号格式不正确
                showMsg("系统提示", "请正确输入手机号");
                return;
            } else {
                showDialog("正在发送验证码……");
                presenter.getCode();
                skm.hide();
                btGetcodeId.setEnabled(false);
                btGetcodeId.setTextColor(Color.GRAY);
                btGetcodeId.setEnabled(false);
                etRegisterCodeId.setEnabled(true);
                etRegisterCodeId.setFocusable(true);
                timer.start();
            }
        } else {
            //输入为空
            showMsg("系统提示", "手机号不能为空");
            return;
        }
    }

    //计时器
    private CountDownTimer timer = new CountDownTimer(60000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btGetcodeId.setText(millisUntilFinished/1000+"秒后重新获取");
        }

        @Override
        public void onFinish() {
            btGetcodeId.setEnabled(true);
            btGetcodeId.setTextColor(Color.parseColor("#00A0E8"));
            btGetcodeId.setText("获取验证码");
        }
    };

    private void isInvalidInput() {
        if (!TextUtils.isEmpty(getMobile())||!TextUtils.isEmpty(getCode())) {
            if (!StringHandler.testPhone(getMobile())) {
                //手机号格式不正确
                showMsg("系统提示", "请正确输入手机号");
                return;
            } else {
                nextStep();
            }
        } else {
            //输入为空
            showMsg("系统提示", "手机号或验证码不能为空");
            return;
        }
    }

    private void nextStep() {
        showDialog("正在检测验证码……");
        presenter.onNext();
    }

    @Override
    public String getMobile() {
        return etRegisterUsernameId.getText().toString().trim();
    }

    @Override
    public void setCode(String code) {
        hideDialog();
        if(code != null && code.length() > 0)
            etRegisterCodeId.setText(code);
    }

    @Override
    public String getCode() {
        return etRegisterCodeId.getText().toString().trim();
    }

    @Override
    public void toPwd2(String token) {
        hideDialog();
        spt.putString(SptConfig.LOGIN_TOKEN, token);
        Intent intent = new Intent(UserUpdatePwd.this,UserUpdatePwd2.class);
        intent.putExtra("mobile",getMobile());
        intent.putExtra("sms_code",getCode());
        startActivity(intent);
        this.finish();
    }

    @Override
    public void toUserLoginActivity() {
        hideDialog();
        this.finish();
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", ""+msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        SmsReciver.setOnSmsRecived(null);
        super.onDestroy();
    }
}
