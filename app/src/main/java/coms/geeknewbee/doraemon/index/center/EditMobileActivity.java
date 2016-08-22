package coms.geeknewbee.doraemon.index.center;

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
import coms.geeknewbee.doraemon.index.center.presenter.EditMobilePresenter;
import coms.geeknewbee.doraemon.index.center.view.IEditMobileView;
import coms.geeknewbee.doraemon.register_login.SmsReciver;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/4/7
 */
public class EditMobileActivity extends BaseActivity implements IEditMobileView {

    /**---------------------组件---------------------**/
    // 手机号的标题
    private TextView et_info;
    private ImageButton ibBack;
    private EditText etOldMobile;
    private EditText etCode;
    private TextView btGetcode;
    private Button btNext;

    /**
     * 4，老号验证，5，新号验证
     */
    int status = 4;

    private void assignViews() {
        et_info = (TextView)findViewById(R.id.et_info);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        etOldMobile = (EditText) findViewById(R.id.et_old_mobile);
        etCode = (EditText) findViewById(R.id.et_code);
        btGetcode = (TextView) findViewById(R.id.bt_getcode);
        btNext = (Button) findViewById(R.id.bt_next);

        et_info.setText("请输入原手机号码");
        if(session.contains(session.USER)){
            UserBean user = (UserBean)session.get(session.USER);
            etOldMobile.setText("" + user.getMobile());
            etOldMobile.setEnabled(false);
        } else {
            finish();
        }
    }

    EditMobilePresenter presenter = new EditMobilePresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mobile1);
        assignViews();
        initListener();
    }

    private void initListener() {
        //返回键
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMobileActivity.this.finish();
            }
        });
        //下一步
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("正在提交验证……");
                if(status == 4){
                    presenter.checkCode();
                } else {
                    presenter.updateMoblie();
                }
            }
        });
        btGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取验证码
                CheckNull();
            }
        });
        SmsReciver.setOnSmsRecived(new SmsReciver.OnSmsRecived() {
            @Override
            public void sendCode(String code) {
                hideDialog();
                etCode.setText(code);
            }
        });
    }

    //计时器
    private CountDownTimer timer = new CountDownTimer(60000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btGetcode.setText(millisUntilFinished/1000+"秒后重新获取");
        }

        @Override
        public void onFinish() {
            btGetcode.setEnabled(true);
            btGetcode.setTextColor(Color.parseColor("#00A0E8"));
            btGetcode.setText("获取验证码");
        }
    };

    private void CheckNull() {
        if (!TextUtils.isEmpty(getMobile())) {
            if (!StringHandler.testPhone(getMobile())) {
                //手机号格式不正确
                showMsg("输入错误", "请正确输入手机号");
                return;
            } else {
                showDialog("正在发送验证码……");
                presenter.getCode();
            }
        } else {
            //输入为空
            showMsg("输入错误", "手机号不能为空");
            return;
        }
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close_left, 0);
    }

    @Override
    public String getMobile() {
        return etOldMobile.getText().toString().trim();
    }

    @Override
    public String getCode() {
        return etCode.getText().toString();
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public void sendSuccess() {
        hideDialog();
        etCode.setEnabled(true);
        etCode.setFocusable(true);
        btGetcode.setEnabled(false);
        btGetcode.setTextColor(Color.GRAY);
        timer.start();//计时器
    }

    @Override
    public void checkSuccess(String token) {
        hideDialog();
        if(status == 4){
            spt.putString(SptConfig.LOGIN_TOKEN, token);
            status = 5;
            et_info.setText("请输入新手机号码");
            etOldMobile.setText("");
            etOldMobile.setEnabled(true);
            etOldMobile.setFocusable(true);
            etOldMobile.requestFocus();
            etCode.setText("");
            btNext.setText("确定");
            timer.cancel();
            timer.onFinish();
        } else if(status == 5){
            UserBean user = (UserBean)session.get(session.USER);
            user.setMobile(getMobile().trim());
            tt.showMessage("更换手机号成功！", tt.LONG);
            finish();
        }
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        SmsReciver.setOnSmsRecived(null);
        super.onDestroy();
    }
}
