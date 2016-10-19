package coms.geeknewbee.doraemon.register_login.presenter;

import android.os.Handler;

import coms.geeknewbee.doraemon.register_login.biz.IUserNextStepBiz;
import coms.geeknewbee.doraemon.register_login.biz.impl.UserNextStepBiz;
import coms.geeknewbee.doraemon.register_login.view.IUserNextStepView;
import coms.geeknewbee.doraemon.utils.ILog;


/**
 * Created by chen on 2016/3/25
 */
public class UserNextStepPresenter {


    private IUserNextStepBiz userBiz;
    private IUserNextStepView userRegisterView;

    private Handler mHandler = new Handler();

    public UserNextStepPresenter(IUserNextStepView userRegisterView) {
        this.userRegisterView = userRegisterView;
        this.userBiz = new UserNextStepBiz();
    }

    /**
     * 获取验证码
     */
    public void getCode() {
        userBiz.getCode(userRegisterView.getMobile(), new IUserNextStepBiz.OnGetCodeListener() {
            @Override
            public void getCodeSuccess(final String code) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        userRegisterView.setCode(code);
                    }
                });
            }

            @Override
            public void getCOdeFailed(String msg) {
                ILog.i("getCOdeFailed().................","Failed.....................\n"+msg);
                userRegisterView.getCodeFailed();
                userRegisterView.showMessage(""+msg);
            }
        });
    }

    /**
     * 注册提交
     */
    public void nextStep() {
            userBiz.nextStep(userRegisterView.getMobile(), userRegisterView.getCode(), new IUserNextStepBiz.OnNextListener() {
                @Override
                public void nextSuccess(String token) {
                    userRegisterView.toRegisterActivity(token);
                }

                @Override
                public void nextFailed(String msg) {
                    ILog.i("token------------------------>", "----------------------->Failed");
                    userRegisterView.showMessage(""+msg);
                }
            });

    }
}
