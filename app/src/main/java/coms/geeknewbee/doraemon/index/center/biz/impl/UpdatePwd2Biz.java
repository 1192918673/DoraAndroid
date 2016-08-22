package coms.geeknewbee.doraemon.index.center.biz.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.index.center.biz.IUpdatePwd2Biz;
import coms.geeknewbee.doraemon.register_login.bean.GetCodeBean;
import coms.geeknewbee.doraemon.register_login.bean.GetTokenBean;

/**
 * Created by chen on 2016/4/6
 */
public class UpdatePwd2Biz implements IUpdatePwd2Biz {
    @Override
    public void reset(final String mobile, final String sms_code, final String new_pwd, final OnResetLisetener lisetener) {
        new Thread(){
            @Override
            public void run() {
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configCurrentHttpCacheExpiry(5*1000);//超时时间
                RequestParams params = new RequestParams();
                params.addBodyParameter("mobile",mobile);
                params.addBodyParameter("sms_code", sms_code);
                params.addBodyParameter("password",new_pwd);
                StringBuffer url = new StringBuffer(GlobalContants.AUTH_URL);
                url.append("/password");
                httpUtils.send(HttpRequest.HttpMethod.PUT, url.toString(), params, new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        //解析response中的sms_code
                        Gson gson = new Gson();
                        HttpBean<GetTokenBean> bean =
                                HttpBean.getBeanFromGson(responseInfo.result.toString(),
                                        new TypeToken<HttpBean<GetTokenBean>>(){});
                        try {
                            String token = bean.getData().getToken();
                            lisetener.resetSuccess(token);
                        } catch (Exception e) {
                            lisetener.resetFailed();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        lisetener.resetFailed();
                    }
                });
            }
        }.start();
    }
}
