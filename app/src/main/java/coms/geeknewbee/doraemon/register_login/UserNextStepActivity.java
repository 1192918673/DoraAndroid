package coms.geeknewbee.doraemon.register_login;

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
import android.widget.Toast;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.register_login.presenter.UserNextStepPresenter;
import coms.geeknewbee.doraemon.register_login.view.IUserNextStepView;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;


public class UserNextStepActivity extends BaseActivity implements IUserNextStepView {

    private ImageButton ibBack;
    private EditText etRegisterUsernameId;
    private EditText etRegisterCodeId;
    private TextView btGetcodeId;
    private Button btRegisterId;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        etRegisterUsernameId = (EditText) findViewById(R.id.et_register_username_id);
        etRegisterCodeId = (EditText) findViewById(R.id.et_register_code_id);
        btGetcodeId = (TextView) findViewById(R.id.bt_getcode_id);
        btRegisterId = (Button) findViewById(R.id.bt_register_id);
        skm = new SoftKeyboardManager(etRegisterCodeId);
    }

    //presenter...
    private UserNextStepPresenter mUserRegisterPresenter = new UserNextStepPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_step);
//        ButterKnife.bind(this);
        assignViews();
        initListener();
    }

    public void initListener() {
        //返回按钮
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSplash2Activity();
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
                etRegisterCodeId.setText(code);
            }
        });

        etRegisterUsernameId.setOnFocusChangeListener(focusChangeListener);
        etRegisterCodeId.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                skm.show();
            }
        }
    };

    //计时器
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btGetcodeId.setText(millisUntilFinished / 1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            btGetcodeId.setEnabled(true);
            btGetcodeId.setTextColor(Color.parseColor("#00A0E8"));
            btGetcodeId.setText("获取验证码");
        }
    };

    private void toSplash2Activity() {
        startActivity(new Intent(UserNextStepActivity.this, Splash2Activity.class));
        this.finish();
    }

    private void CheckNull() {
        if (!TextUtils.isEmpty(getMobile())) {
            if (!StringHandler.testPhone(getMobile())) {
                //手机号格式不正确
                showMsg("输入错误", "请正确输入手机号");
                return;
            } else {
                btGetcodeId.setEnabled(false);
                btGetcodeId.setTextColor(Color.GRAY);
                mUserRegisterPresenter.getCode();
                timer.start();//计时器
                skm.hide();
                showDialog("正在发送验证码……");
//                etRegisterCodeId.setEnabled(true);
//                etRegisterCodeId.setFocusable(true);
            }
        } else {
            //输入为空
            showMsg("输入错误", "手机号不能为空");
            return;
        }
    }

    private void isInvalidInput() {
        if (!TextUtils.isEmpty(getMobile()) || !TextUtils.isEmpty(getCode())) {
            if (!StringHandler.testPhone(getMobile())) {
                //手机号格式不正确
                showMsg("输入错误", "请正确输入手机号");
                return;
            } else {
                skm.hide();
                mUserRegisterPresenter.nextStep();
                showDialog("正在注册……");
            }
        } else {
            //输入为空
            showMsg("输入错误", "手机号或验证码不能为空");
            return;
        }
    }

    @Override
    public String getMobile() {
        return etRegisterUsernameId.getText().toString().trim();
    }

    @Override
    public void setCode(String code) {
        //SystemClock.sleep(2000);
        etRegisterCodeId.setEnabled(true);
        etRegisterCodeId.setFocusable(true);
        hideDialog();
        if (code != null && code.length() > 0)
            etRegisterCodeId.setText(code);
    }

    @Override
    public void getCodeFailed() {
        timer.cancel();
    }

    @Override
    public String getCode() {
        return etRegisterCodeId.getText().toString().trim();
    }

    @Override
    public void toRegisterActivity(String token) {
        hideDialog();
        SharePreferenceUtils.putString(this, "token", token);
        startActivity(new Intent(UserNextStepActivity.this, UserRegisterActivity.class));
        this.finish();
    }

    @Override
    public void toUserLoginActivity() {
        startActivity(new Intent(UserNextStepActivity.this, UserLoginActivity.class));
        this.finish();
    }

    @Override
    public void showFailedError() {
        Toast.makeText(getApplicationContext(), "Failed..............", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        toSplash2Activity();
        this.finish();
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.fade_in_popup, R.anim.fade_out_popup);
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        SmsReciver.setOnSmsRecived(null);
        super.onDestroy();
    }
}
