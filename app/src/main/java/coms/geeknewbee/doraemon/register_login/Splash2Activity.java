package coms.geeknewbee.doraemon.register_login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.utils.Session;

/**
 * Created by chen on 2016/4/6
 * 存在问题：在登录或注册界面，点击返回后，此处界面已经不存在
 */
public class Splash2Activity extends BaseActivity {

    private ImageButton toEgister;
    private TextView toLogin;

    private void assignViews() {
        toEgister = (ImageButton) findViewById(R.id.to_egister);
        toLogin = (TextView) findViewById(R.id.to_login);

        //初始化个推SDK
        initGeTui();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        assignViews();
        initListener();
    }

    private void initListener() {
        toEgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Splash2Activity.this, UserNextStepActivity.class));
                Splash2Activity.this.finish();
            }
        });
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Splash2Activity.this, UserLoginActivity.class));
                Splash2Activity.this.finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    private void initGeTui() {
        PushManager.getInstance().initialize(MyApplication.getContext());
        String Clientid = PushManager.getInstance().getClientid(this);
        if(Clientid == null){
            Clientid = spt.getString(Session.CLIENT_ID, null);
        } else {
            spt.putString(Session.CLIENT_ID, Clientid);
        }
        session.put(Session.CLIENT_ID, Clientid);
    }
}
