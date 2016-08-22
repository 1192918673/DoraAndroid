package coms.geeknewbee.doraemon.register_login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.chat.EMClient;
import com.igexin.sdk.PushManager;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;

/**
 * Created by chen on 2016/4/31
 *
 *      1.闪屏页面检查是否存在token，若存在，进入IndexActivity
 *                             若不存在，进入登录页面
 *
 */
public class SplashActivity extends BaseActivity {

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    startActivity(new Intent(SplashActivity.this, Splash2Activity.class));
                    SplashActivity.this.finish();
                    break;
                case 2:
                    startActivity(new Intent(SplashActivity.this, IndexActivity.class));
                    SplashActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //初始化个推SDK
        initGeTui();

        CheckLogin();

        //环信：保证进入主页面后本地会话和群组都load完毕。
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();
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

    /**
     * 检查是否存在token，若存在，进入IndexActivity
     *                             若不存在，进入登录页面
     */
    private void CheckLogin() {
        String token = spt.getString(SptConfig.LOGIN_TOKEN, null);
            if (null == token) {
                //1.进入splash2页面
                mHandler.sendEmptyMessageDelayed(1, 2000);
            } else {
                //2.进入index页面
                mHandler.sendEmptyMessageDelayed(2, 2000);
            }
    }
}
