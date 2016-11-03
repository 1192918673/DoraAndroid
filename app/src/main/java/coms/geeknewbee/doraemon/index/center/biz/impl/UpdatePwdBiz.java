package coms.geeknewbee.doraemon.index.center.biz.impl;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.index.center.biz.IUpdatePwdBiz;
import coms.geeknewbee.doraemon.register_login.bean.GetCodeBean;

/**
 * Created by chen on 2016/4/6
 */
public class UpdatePwdBiz implements IUpdatePwdBiz {
    @Override
    public void getCode(final String mobile, final OnGetCodeListener listener) {
        new Thread(){
            @Override
            public void run() {
                StringBuffer url = new StringBuffer(GlobalContants.GET_CODE);
                url.append("?mobile="+mobile+"&type=1");
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.send(HttpRequest.HttpMethod.GET, url.toString(), new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        //解析response中的sms_code
                        HttpBean<GetCodeBean> bean =
                                HttpBean.getBeanFromGson(responseInfo.result.toString(),
                                        new TypeToken<HttpBean<GetCodeBean>>(){});
                        String sms_code = bean.getData().getSms_code();
                        listener.getCodeSuccess(sms_code);
                    }
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        listener.getCOdeFailed(msg+error);
                    }
                });
            }
        }.start();
    }

    @Override
    public void nextStep(String mobile, String code, OnNextListener listener) {

    }
}
