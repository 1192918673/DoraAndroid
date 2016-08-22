package coms.geeknewbee.doraemon.register_login.bean;

/**
 * Created by chen on 2016/3/25
 * 解析注册返回的token的bean
 */
public class GetTokenBean {
    public String token;
    public int expires_in;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}
