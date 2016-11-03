package coms.geeknewbee.doraemon.register_login.biz.impl;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.global.IHttpListener;
import coms.geeknewbee.doraemon.register_login.bean.GetTokenBean;
import coms.geeknewbee.doraemon.register_login.biz.IUserLoginBiz;
import coms.geeknewbee.doraemon.utils.Session;

/**
 * Created by chen on 2016/3/28
 */
public class UserLoginBiz implements IUserLoginBiz {
    @Override
    public void login(final String mobile, final String pwd, final IHttpListener listener) {
        new Thread() {
            @Override
            public void run() {
                StringBuffer url = new StringBuffer(GlobalContants.GET_TOKEN);
                url.append("?mobile=" + mobile + "&password=" + pwd + "&cid="
                        + Session.getSession().get(Session.CLIENT_ID) + "&client_type=1");
                HttpUtils httpUtils = new HttpUtils();

                httpUtils.send(HttpRequest.HttpMethod.GET, url.toString(), new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        //解析token
                        HttpBean<GetTokenBean> bean =
                                HttpBean.getBeanFromGson(responseInfo.result.toString(),
                                        new TypeToken<HttpBean<GetTokenBean>>(){});
                        if(bean.getCode() == 200){
                            String token = bean.getData().getToken();
                            Map<String, Object> data = new HashMap<String, Object>();
                            data.put("token", token);
                            listener.success(data);
                        } else {
                            listener.failed(bean.getMsg());
                        }
                    }
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        listener.failed(msg);
                    }
                });
            }
        }.start();
    }
}
