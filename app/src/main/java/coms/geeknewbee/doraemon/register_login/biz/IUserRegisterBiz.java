package coms.geeknewbee.doraemon.register_login.biz;

/**
 * Created by chen on 2016/3/28
 */
public interface IUserRegisterBiz {

    /**
     * 注册
     * @param pwd 密码
     * @param token token
     * @param listener 监听
     */
    void register(String pwd, String token, OnRegisterListener listener);

    interface OnRegisterListener{
        /**
         * 注册成功
         */
        void registerSuccess(String token);

        /**
         * 注册失败
         */
        void registerFailed(String msg);
    }

}
