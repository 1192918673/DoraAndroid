package coms.geeknewbee.doraemon.register_login.biz;

/**
 * Created by chen on 2016/4/6
 */
public interface IResetPwdBiz {

    void getCode(String mobile, OnGetCodeListener listener);
    interface OnGetCodeListener {
        /**
         * 获取验证码成功调用
         */
        void getCodeSuccess(String code);

        /**
         * 获取验证失败调用
         */
        void getCOdeFailed(String msg);
    }

    void nextStep(String mobile, String code, OnNextListener listener);

    interface OnNextListener {
        /**
         * 成功调用
         *
         * @param token
         */
        void nextSuccess(String token);
        /**
         * 失败调用
         */
        void nextFailed(String msg);
    }
}
