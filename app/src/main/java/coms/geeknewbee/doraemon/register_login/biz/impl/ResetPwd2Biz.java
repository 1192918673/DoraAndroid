package coms.geeknewbee.doraemon.register_login.biz.impl;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.register_login.bean.GetTokenBean;
import coms.geeknewbee.doraemon.register_login.biz.IResetPwd2Biz;

/**
 * Created by chen on 2016/4/6
 */
public class ResetPwd2Biz implements IResetPwd2Biz {
    @Override
    public void reset(final String token, final String new_pwd, final OnResetLisetener lisetener) {
        new Thread(){
            @Override
            public void run() {
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configCurrentHttpCacheExpiry(5*1000);//超时时间
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("password", new_pwd);
                StringBuffer url = new StringBuffer(GlobalContants.CREATE_PWD);
                httpUtils.send(HttpRequest.HttpMethod.POST, url.toString(), params, new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        //解析response中的sms_code
                        HttpBean<GetTokenBean> bean =
                                HttpBean.getBeanFromGson(responseInfo.result.toString(),
                                        new TypeToken<HttpBean<GetTokenBean>>(){});
                        if(bean.getCode() == 200){
                            String token = bean.getData().getToken();
                            lisetener.resetSuccess(token);
                        } else {
                            lisetener.resetFailed(bean.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        lisetener.resetFailed(msg);
                    }
                });
            }
        }.start();
    }
}
