package coms.geeknewbee.doraemon.register_login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.register_login.presenter.UserLoginPresenter;
import coms.geeknewbee.doraemon.register_login.view.IUserLoginView;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/3/28
 */
public class UserLoginActivity extends BaseActivity implements IUserLoginView {

    private EditText etMobile;
    private EditText etPassword;
    private Button btLogin;
    private TextView tvException;

    private void assignViews() {
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etPassword = (EditText) findViewById(R.id.et_password);
        btLogin = (Button) findViewById(R.id.bt_login);
        tvException = (TextView) findViewById(R.id.tv_exception);
        skm = new SoftKeyboardManager(etMobile);
    }


    UserLoginPresenter userLoginPresenter = new UserLoginPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        assignViews();
        initListener();
    }


    private void initListener() {
        //登录按钮的事件
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInvalidInput();
            }
        });
        tvException.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码页面
                startActivity(new Intent(UserLoginActivity.this,UserResetPwd.class));
                UserLoginActivity.this.finish();
            }
        });
        etMobile.setOnFocusChangeListener(focusChangeListener);
        etPassword.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };

    //验证用户输入
    private void isInvalidInput() {
        /**
         * 1.手机号格式不正确:正输入正确账号;
         * 2.手机号格式正确->密码不正确:账号或密码错误,请重新输入;
         */
        if(TextUtils.isEmpty(getMoblie())|| TextUtils.isEmpty(getPwd())){
            //输入为空
            showMsg("输入错误","手机号或密码不能为空");
            return;
        }else {
            //输入没空
            if (StringHandler.testPhone(getMoblie())) {
                userLoginPresenter.login();
                skm.hide();
                showDialog("正在登录……");
            }else {
                //手机号格式不正确
                showMsg("输入错误","请正确输入手机号");
                return;
            }
        }
    }

    @Override
    public void showError() {
        hideDialog();
        showMsg("输入错误","手机号或密码不正确");
        return;
    }

    @Override
    public String getMoblie() {
        return etMobile.getText().toString().trim();
    }

    @Override
    public String getPwd() {
        return etPassword.getText().toString().trim();
    }

    @Override
    public void toRegister() {
        hideDialog();
        startActivity(new Intent(UserLoginActivity.this, UserNextStepActivity.class));
        this.finish();
    }

    @Override
    public void toIndexActivity() {
        hideDialog();
        startActivity(new Intent(UserLoginActivity.this, IndexActivity.class));
        this.finish();
    }

    @Override
    public void setToken(String token) {
        spt.putString(SptConfig.LOGIN_TOKEN, token);
    }


    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", ""+msg);
    }
}
