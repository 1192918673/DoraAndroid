package coms.geeknewbee.doraemon.index.center;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.register_login.presenter.ResetPwd2Presenter;
import coms.geeknewbee.doraemon.register_login.view.IResetPwd2View;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;

/**
 * Created by chen on 2016/4/6
 */
public class UserUpdatePwd2 extends BaseActivity implements IResetPwd2View {

    private ImageButton ibBack;
    private EditText etPwd;
    private TextView tvv1;
    private EditText etPwd2;
    private TextView tvWarming;
    private Button btReset;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        tvv1 = (TextView) findViewById(R.id.tvv1);
        etPwd2 = (EditText) findViewById(R.id.et_pwd2);
        tvWarming = (TextView) findViewById(R.id.tv_warming);
        btReset = (Button) findViewById(R.id.bt_reset);
        skm = new SoftKeyboardManager(etPwd);
    }

    private String mobile;
    private String sms_code;

    ResetPwd2Presenter pwd2Presenter = new ResetPwd2Presenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_2);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        sms_code = intent.getStringExtra("sms_code");
        assignViews();
        initListener();
    }

    private void initListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd2Presenter.reset();
                skm.hide();
                showDialog("正在提交新密码……");
            }
        });
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public String getsms_Code() {
        return sms_code;
    }

    @Override
    public String getPassword() {
        return etPwd.getText().toString().trim();
    }

    @Override
    public void setToken(String token) {
        hideDialog();
        spt.putString(SptConfig.LOGIN_TOKEN,token);
        finish();
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public void goBack() {
        hideDialog();
        startActivity(new Intent(UserUpdatePwd2.this,UserUpdatePwd.class));
        this.finish();
    }

    @Override
    public void toLogin() {
        Toast.makeText(MyApplication.getContext(), "密码重置成功，请妥善保管", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void showMessage(String msg) {
        tt.showMessage("" + msg, tt.LONG);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }
}
