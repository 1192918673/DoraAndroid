package coms.geeknewbee.doraemon.register_login.biz;


/**
 * Created by chen on 2016/3/25
 * 实现根据手机号返回验证码功能。
 */
public interface IUserNextStepBiz {


    /**
     * 获取验证码的抽象，调用获取注册码的监听
     * @param mobile
     * @param listener
     */
    void getCode(String mobile, OnGetCodeListener listener);

    /**
     * 下一步的抽象，调用注册监听接口
     *
     * @param mobile
     * @param code
     * @param listener
     */
    void nextStep(String mobile, String code, OnNextListener listener);

    //////////////////////////////////////////监听/////////////////////////////////////////////////

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
}
