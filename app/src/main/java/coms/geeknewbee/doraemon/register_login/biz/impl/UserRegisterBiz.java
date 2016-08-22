package coms.geeknewbee.doraemon.register_login.biz.impl;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.register_login.bean.GetCodeBean;
import coms.geeknewbee.doraemon.register_login.bean.GetTokenBean;
import coms.geeknewbee.doraemon.register_login.biz.IUserRegisterBiz;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by chen on 2016/3/28
 */
public class UserRegisterBiz implements IUserRegisterBiz {
    @Override
    public void register(final String pwd, final String token, final OnRegisterListener listener) {
        new Thread(){
            @Override
            public void run() {
                //设置请求参数
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configCurrentHttpCacheExpiry(5*1000);//超时时间
                RequestParams params = new RequestParams();
                params.addBodyParameter("password", pwd);
                params.addBodyParameter("token",token);
                StringBuffer url = new StringBuffer(GlobalContants.CREATE_PWD);
                url.append("?token=" + token);
                httpUtils.send(HttpRequest.HttpMethod.POST, url.toString(),params, new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        HttpBean<GetTokenBean> bean =
                                HttpBean.getBeanFromGson(responseInfo.result.toString(),
                                        new TypeToken<HttpBean<GetTokenBean>>(){});
                        if(bean.getCode() == 200){
                            String token = bean.getData().getToken();
                            listener.registerSuccess(token);
                        } else {
                            listener.registerFailed(bean.getMsg());
                        }
                    }
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        listener.registerFailed(msg);
                    }
                });
            }
        }.start();
    }
}
