package coms.geeknewbee.doraemon.register_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.register_login.presenter.UserRegisterPresenter;
import coms.geeknewbee.doraemon.register_login.view.IUserRegisterView;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;

/**
 * Created by chen on 2016/3/28
 */
public class UserRegisterActivity extends BaseActivity implements IUserRegisterView {


    private ImageButton ibBack;
    private TextView tvMobile;
    private EditText etPwd;
    private EditText etRepwd;
    private Button btRegisterPwd;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvMobile = (TextView) findViewById(R.id.tv_mobile);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etRepwd = (EditText) findViewById(R.id.et_repwd);
        btRegisterPwd = (Button) findViewById(R.id.bt_register_pwd);
        skm = new SoftKeyboardManager(etPwd);
    }



    UserRegisterPresenter userRegisterPresenter = new UserRegisterPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pwd);
//        ButterKnife.bind(this);
        assignViews();
        initListener();

    }

    private void initListener() {
        //注册按钮的点击事件
        btRegisterPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInvalidInput();
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserRegisterActivity.this,UserNextStepActivity.class));
                UserRegisterActivity.this.finish();
            }
        });
        etPwd.setOnFocusChangeListener(focusChangeListener);
        etRepwd.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };

    @Override
    public String getPwd() {
        return etPwd.getText().toString().trim();
    }

    @Override
    public String getRePwd() {
        return etRepwd.getText().toString().trim();
    }

    public void isInvalidInput() {
        if (TextUtils.isEmpty(getPwd()) || TextUtils.isEmpty(getRePwd())) {
            //输入为空
            showMsg("输入错误", "密码不能为空");
            return;
        } else {
            if (getPwd().equals(getRePwd())) {
                userRegisterPresenter.register();
                skm.hide();
                showDialog("正在提交密码……");
            } else {
                showMsg("输入错误", "两次输入密码不一致");
                return;
            }
        }
    }

    @Override
    public String getToken() {
        return SharePreferenceUtils.getString(getApplicationContext(), "token", null);
    }

    @Override
    public void toIndexActivity() {
        startActivity(new Intent(UserRegisterActivity.this, IndexActivity.class));
        this.finish();
    }

    @Override
    public void showFailedError() {
        hideDialog();
        Toast.makeText(getApplicationContext(), "Failed..............", Toast.LENGTH_SHORT).show();
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
    }

    @Override
    public void setToken(String token) {
        hideDialog();
        spt.putString(SptConfig.LOGIN_TOKEN, token);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }
}
