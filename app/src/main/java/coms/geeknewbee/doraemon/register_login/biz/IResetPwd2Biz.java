package coms.geeknewbee.doraemon.register_login.biz;

/**
 * Created by chen on 2016/4/6
 */
public interface IResetPwd2Biz {
    void reset(String token, String new_pwd, OnResetLisetener lisetener);

    interface  OnResetLisetener{
        void resetSuccess(String token);
        void resetFailed(String msg);
    }
}
