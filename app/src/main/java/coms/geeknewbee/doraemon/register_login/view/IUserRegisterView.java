package coms.geeknewbee.doraemon.register_login.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/3/28
 */
public interface IUserRegisterView extends IBaseView {

    /**
     * 获取pwd
     */
    String getPwd();

    /**
     * 获取确认密码
     */
    String getRePwd();

    /**
     * 获取token
     */
    String getToken();

    /**
     * 跳转主页面
     */
    void toIndexActivity();


    /**
     * 显示失败信息
     */
    void showFailedError();

    /**
     * 设置token
     */
    void setToken(String token);

}

