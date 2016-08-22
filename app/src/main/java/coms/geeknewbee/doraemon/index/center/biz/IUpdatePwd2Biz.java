package coms.geeknewbee.doraemon.index.center.biz;

/**
 * Created by chen on 2016/4/6
 */
public interface IUpdatePwd2Biz {

    void reset(String mobile, String sms_code, String new_pwd, OnResetLisetener lisetener);

    interface  OnResetLisetener{
        void resetSuccess(String token);
        void resetFailed();
    }
}
