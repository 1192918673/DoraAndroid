package coms.geeknewbee.doraemon.register_login.presenter;

import coms.geeknewbee.doraemon.register_login.biz.IResetPwd2Biz;
import coms.geeknewbee.doraemon.register_login.biz.impl.ResetPwd2Biz;
import coms.geeknewbee.doraemon.register_login.view.IResetPwd2View;

/**
 * Created by chen on 2016/4/6
 */
public class ResetPwd2Presenter {

    private IResetPwd2Biz resetPwd2Biz;
    private IResetPwd2View resetPwd2View;

    public ResetPwd2Presenter(IResetPwd2View resetPwd2View){
        this.resetPwd2View = resetPwd2View;
        this.resetPwd2Biz = new ResetPwd2Biz();
    }

    public void reset(){
        resetPwd2Biz.reset(resetPwd2View.getToken(), resetPwd2View.getPassword(), new IResetPwd2Biz.OnResetLisetener() {
            @Override
            public void resetSuccess(String token) {
                resetPwd2View.setToken(token);
                resetPwd2View.toLogin();
            }

            @Override
            public void resetFailed(String msg) {
                resetPwd2View.showMessage(msg);
                resetPwd2View.goBack();
            }
        });
    }

}
