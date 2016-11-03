package coms.geeknewbee.doraemon.box.sendvoice.presenter;

import coms.geeknewbee.doraemon.box.sendvoice.biz.ISendVoiceBiz;
import coms.geeknewbee.doraemon.box.sendvoice.view.ISendVoiceView;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by chen on 2016/3/30
 */
public class SendVoicePresenter {
    ISendVoiceBiz sendVoiceBiz;
    ISendVoiceView sendVoiceView;

    public SendVoicePresenter(ISendVoiceView sendVoiceView){
        this.sendVoiceView = sendVoiceView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sendVoiceBiz = retrofit.create(ISendVoiceBiz.class);
    }

    /**
     * 发送文本到机器猫
     */
    public void sendText(){
        String pk = sendVoiceView.getRobotPk();
        String token = sendVoiceView.getToken();
        String text = sendVoiceView.getText();

        Call<HttpBean> call = sendVoiceBiz.sendText(pk, token, text);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        sendVoiceView.showSuccess();
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        sendVoiceView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        sendVoiceView.showFailed("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        sendVoiceView.showFailed("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        sendVoiceView.showFailed("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                sendVoiceView.showFailed("" + t.getMessage());
            }
        });
    }
}
