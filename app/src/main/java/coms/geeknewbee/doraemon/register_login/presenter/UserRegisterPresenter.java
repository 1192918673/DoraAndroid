package coms.geeknewbee.doraemon.register_login.presenter;

import coms.geeknewbee.doraemon.register_login.biz.IUserRegisterBiz;
import coms.geeknewbee.doraemon.register_login.biz.impl.UserRegisterBiz;
import coms.geeknewbee.doraemon.register_login.view.IUserRegisterView;

/**
 * Created by chen on 2016/3/28
 */
public class UserRegisterPresenter {
    IUserRegisterBiz userRegisterBiz;
    IUserRegisterView userRegisterView;

    public UserRegisterPresenter(IUserRegisterView userRegisterView) {
        this.userRegisterView = userRegisterView;
        userRegisterBiz = new UserRegisterBiz();
    }

    /**
     * 注册
     */
    public void register() {
        userRegisterBiz.register(userRegisterView.getPwd(), userRegisterView.getToken(), new IUserRegisterBiz.OnRegisterListener() {
            @Override
            public void registerSuccess(String token) {
                //注册成功，跳转到主页面
                if(token != null && token.length() > 0)
                    userRegisterView.setToken(token);
                userRegisterView.toIndexActivity();
            }

            @Override
            public void registerFailed(String msg) {
                userRegisterView.showMessage(msg);
            }
        });
    }
}
