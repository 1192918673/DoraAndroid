package coms.geeknewbee.doraemon.index.center.presenter;

import android.os.Handler;

import coms.geeknewbee.doraemon.index.center.biz.IUpdatePwdBiz;
import coms.geeknewbee.doraemon.index.center.biz.impl.UpdatePwdBiz;
import coms.geeknewbee.doraemon.index.center.view.IUpdatePwdView;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by chen on 2016/4/6
 */
public class UpdatePwdPresenter {

    private IUpdatePwdBiz updatePwdBiz;
    private IUpdatePwdView updatePwdView;
    private Handler mHandler = new Handler();

    public UpdatePwdPresenter(IUpdatePwdView updatePwdView){
        this.updatePwdView = updatePwdView;
        this.updatePwdBiz = new UpdatePwdBiz();
    }

    public void getCode(){
        updatePwdBiz.getCode(updatePwdView.getMobile(), new IUpdatePwdBiz.OnGetCodeListener() {
            @Override
            public void getCodeSuccess(final String code) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updatePwdView.setCode(code);
                    }
                });
            }

            @Override
            public void getCOdeFailed(String msg) {
                ILog.i("getCOdeFailed().................", "Failed.....................\n" + msg);

            }
        });
    }
}
