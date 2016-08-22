package coms.geeknewbee.doraemon.index.center.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/4/7
 */
public interface IEditMobileView extends IBaseView {
    /**
     * 获取输入手机号
     * @return 手机号
     */
    String getMobile();

    /**
     * 获取验证码
     */
    String getCode();

    /**
     * 获取token
     */
    String getToken();

    /**
     * 验证状态
     * @return
     */
    int getStatus();

    /**
     * 发送验证码成功
     */
    void sendSuccess();

    /**
     * 验证成功
     */
    void checkSuccess(String token);
}
