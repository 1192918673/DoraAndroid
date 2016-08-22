package coms.geeknewbee.doraemon.register_login.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/4/6
 */
public interface IResetPwdView extends IBaseView {

    String getMobile();

    void setCode(String code);

    String getCode();

    void toPwd2(String token);

    void toUserLoginActivity();
}
