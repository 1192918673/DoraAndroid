package coms.geeknewbee.doraemon.register_login.biz.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.register_login.bean.GetCodeBean;
import coms.geeknewbee.doraemon.register_login.bean.GetTokenBean;
import coms.geeknewbee.doraemon.register_login.biz.IUserNextStepBiz;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;

/**
 * Created by chen on 2016/3/24
 */

//实现IUser定义的接口的登录方法
public class UserNextStepBiz implements IUserNextStepBiz {
    @Override
    public void getCode(final String mobile , final OnGetCodeListener listener) {
        new Thread(){
            @Override
            public void run() {
                //获取验证码
                StringBuffer url = new StringBuffer(GlobalContants.GET_CODE);
                url.append("?mobile="+mobile+"&type=1");
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.send(HttpRequest.HttpMethod.GET, url.toString(), new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        //解析response中的sms_code
                        Gson gson = new Gson();
                        ILog.e("test", "" + responseInfo.result.toString());
                        HttpBean<GetCodeBean> bean =
                                HttpBean.getBeanFromGson(responseInfo.result.toString(),
                                        new TypeToken<HttpBean<GetCodeBean>>(){});
                        if(bean.getCode() == 200){
                            String sms_code = bean.getData().getSms_code();
                            listener.getCodeSuccess(sms_code);
                        } else {
                            listener.getCOdeFailed(bean.getMsg());
                        }
                    }
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        listener.getCOdeFailed(msg);
                    }
                });
            }
        }.start();
    }

    @Override
    public void nextStep(final String mobile, final String code, final OnNextListener listener) {
        new Thread(){
            @Override
            public void run() {
                //注册提交
                StringBuffer url = new StringBuffer(GlobalContants.GET_TOKEN);
                url.append("?mobile=" + mobile + "&sms_code=" + code + "&cid="
                        + Session.getSession().get(Session.CLIENT_ID) + "&client_type=1");
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.send(HttpRequest.HttpMethod.GET, url.toString(), new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        //解析token
                        Gson gson = new Gson();
                        HttpBean<GetTokenBean> bean =
                                HttpBean.getBeanFromGson(responseInfo.result.toString(),
                                        new TypeToken<HttpBean<GetTokenBean>>(){});
                        if(bean.getCode() == 200){
                            String token = bean.getData().getToken();
                            listener.nextSuccess(token);
                        } else {
                            listener.nextFailed(bean.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        listener.nextFailed(msg);
                    }
                });

            }
        }.start();
    }


}
