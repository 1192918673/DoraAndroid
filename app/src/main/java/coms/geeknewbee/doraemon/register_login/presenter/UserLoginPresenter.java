package coms.geeknewbee.doraemon.register_login.presenter;

import java.util.Map;

import coms.geeknewbee.doraemon.global.IHttpListener;
import coms.geeknewbee.doraemon.register_login.biz.IUserLoginBiz;
import coms.geeknewbee.doraemon.register_login.biz.impl.UserLoginBiz;
import coms.geeknewbee.doraemon.register_login.view.IUserLoginView;

/**
 * Created by chen on 2016/3/28
 */
public class UserLoginPresenter {
    IUserLoginBiz userLoginBiz;
    IUserLoginView userLoginView;

    public UserLoginPresenter(IUserLoginView userLoginView){
        this.userLoginView = userLoginView;
        this.userLoginBiz = new UserLoginBiz();
    }

    /**
     * 登录
     */
    public void login() {
        userLoginBiz.login(userLoginView.getMoblie(), userLoginView.getPwd(), new IHttpListener() {
            @Override
            public void success(Map<String, Object> data) {
                if(data.containsKey("token")){
                    String token = (String)data.get("token");
                    if(token != null && token.length() > 0){
                        userLoginView.setToken(token);
                    }
                }
                userLoginView.toIndexActivity();
            }

            @Override
            public void failed(String msg) {
                userLoginView.showMessage(msg);
            }
        });
    }
}
