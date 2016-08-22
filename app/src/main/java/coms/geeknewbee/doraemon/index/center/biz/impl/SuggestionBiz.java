package coms.geeknewbee.doraemon.index.center.biz.impl;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.index.center.biz.ISuggestionBiz;
import coms.geeknewbee.doraemon.utils.Session;

/**
 * Created by chen on 2016/4/13
 */
public class SuggestionBiz implements ISuggestionBiz {
    @Override
    public void sendSuggestion(final String token, final String content, final onSendListener listener) {
        new Thread() {
            @Override
            public void run() {
                Session session = Session.getSession();
                String url = GlobalContants.SEND_SUGGESTION;
                HttpUtils httpUtils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("token", token);
                params.addBodyParameter("content", content);
                params.addBodyParameter("device_type", "1");
                params.addBodyParameter("app_version", (String)session.get(Session.VERSION_NAME));
                params.addBodyParameter("os_version", (String)session.get(Session.SYSTEM_VERSION_NAME));
                params.addBodyParameter("phone_model", (String)session.get(Session.MODEL));
                httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        listener.sendSuccess();
;                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        listener.sendFailed();
                    }
                });
            }
        }.start();
    }
}
