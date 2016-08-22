package coms.geeknewbee.doraemon.register_login.biz;

import coms.geeknewbee.doraemon.global.IHttpListener;

/**
 * Created by chen on 2016/3/28
 */
public interface IUserLoginBiz {
    void login(String mobile, String pwd, IHttpListener listener);
}
