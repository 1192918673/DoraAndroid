package coms.geeknewbee.doraemon.register_login.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/4/6
 */
public interface IResetPwd2View extends IBaseView {

    String getMobile();

    String getsms_Code();

    String getPassword();

    String getToken();

    void setToken(String token);

    void goBack();

    void toLogin();

}
