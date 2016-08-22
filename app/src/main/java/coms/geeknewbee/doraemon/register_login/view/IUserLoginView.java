package coms.geeknewbee.doraemon.register_login.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/3/28
 */
public interface IUserLoginView extends IBaseView {
    /**
     * 获取mobile
     */
    String getMoblie();

    /**
     * 获取pwd
     */
    String getPwd();
    /**
     * 跳转主页
     */
    void toIndexActivity();

    /**
     * 保存token
     */
    void setToken(String token);

    /**
     * 跳转注册
     */
    void toRegister();

    void showError();



}
