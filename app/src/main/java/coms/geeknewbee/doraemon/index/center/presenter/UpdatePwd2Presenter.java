package coms.geeknewbee.doraemon.index.center.presenter;

import coms.geeknewbee.doraemon.index.center.biz.IUpdatePwd2Biz;
import coms.geeknewbee.doraemon.index.center.biz.impl.UpdatePwd2Biz;
import coms.geeknewbee.doraemon.index.center.view.IUpdatePwd2View;

/**
 * Created by chen on 2016/4/6
 */
public class UpdatePwd2Presenter {

    private IUpdatePwd2Biz resetPwd2Biz;
    private IUpdatePwd2View resetPwd2View;

    public UpdatePwd2Presenter(IUpdatePwd2View resetPwd2View){
        this.resetPwd2View = resetPwd2View;
        this.resetPwd2Biz = new UpdatePwd2Biz();
    }

    public void reset(){
        resetPwd2Biz.reset(resetPwd2View.getMobile(), resetPwd2View.getsms_Code(), resetPwd2View.getPassword(), new IUpdatePwd2Biz.OnResetLisetener() {
            @Override
            public void resetSuccess(String token) {
                resetPwd2View.setToken(token);
            }

            @Override
            public void resetFailed() {

            }
        });
    }

}
