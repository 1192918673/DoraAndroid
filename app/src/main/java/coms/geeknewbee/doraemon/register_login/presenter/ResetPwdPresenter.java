package coms.geeknewbee.doraemon.register_login.presenter;

import android.os.Handler;

import coms.geeknewbee.doraemon.register_login.biz.IResetPwdBiz;
import coms.geeknewbee.doraemon.register_login.biz.impl.ResetPwdBiz;
import coms.geeknewbee.doraemon.register_login.view.IResetPwdView;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/4/6
 */
public class ResetPwdPresenter {

    private IResetPwdBiz resetPwdBiz;
    private IResetPwdView resetPwdView;
    private Handler mHandler = new Handler();

    public ResetPwdPresenter(IResetPwdView resetPwdView){
        this.resetPwdView = resetPwdView;
        this.resetPwdBiz = new ResetPwdBiz();
    }

    public void getCode(){
        String mobile = resetPwdView.getMobile();
        if(!StringHandler.testPhone(mobile)){
            resetPwdView.showMessage("请输入正确的手机号码");
            return;
        }
        resetPwdBiz.getCode(mobile, new IResetPwdBiz.OnGetCodeListener() {
            @Override
            public void getCodeSuccess(final String code) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetPwdView.setCode(code);
                    }
                });
            }

            @Override
            public void getCOdeFailed(String msg) {
                ILog.i("getCOdeFailed().................", "Failed.....................\n" + msg);
                resetPwdView.showMessage(msg);
            }
        });
    }

    public void onNext(){
        String mobile = resetPwdView.getMobile();
        String code = resetPwdView.getCode();
        if(!StringHandler.testPhone(mobile)){
            resetPwdView.showMessage("请输入正确的手机号码");
            return;
        }
        if(StringHandler.isEmpty(code)){
            resetPwdView.showMessage("验证码不能为空");
            return;
        }
        resetPwdBiz.nextStep(mobile, code, new IResetPwdBiz.OnNextListener() {
            @Override
            public void nextSuccess(String token) {
                if(StringHandler.isEmpty(token)){
                    resetPwdView.showMessage("验证错误");
                } else {
                    resetPwdView.toPwd2(token);
                }
            }

            @Override
            public void nextFailed(String msg) {
                resetPwdView.showMessage(msg);
            }
        });
    }
}
