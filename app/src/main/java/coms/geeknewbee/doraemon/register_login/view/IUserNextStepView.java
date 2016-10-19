package coms.geeknewbee.doraemon.register_login.view;


import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/3/25
 */
public interface IUserNextStepView extends IBaseView {

    /**
     * Presenter与View交互是通过接口。所以我们这里需要定义一个ILoginView，
     */
    /**
     * 获取输入手机号
     * @return 手机号
     */
    String getMobile();


    /**
     * 设置验证码
     */
    void setCode(String code);

    /**
     * 获取验证码失败
     */
    void getCodeFailed();

    /**
     * 获取验证码
     */
    String getCode();

    /**
     * Register存在注册成功与失败的处理，我们主要看成功我们是跳转Activity，而失败可能是去给个提醒：
     */

    /**
     * 跳转到注册密码页面
     */
    void toRegisterActivity(String token);


    /**
     * 直接跳转登录页面
     */
    void toUserLoginActivity();

    /**
     * 显示注册失败原因
     */
    void showFailedError();

}
