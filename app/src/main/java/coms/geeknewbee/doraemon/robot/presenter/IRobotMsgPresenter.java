package coms.geeknewbee.doraemon.robot.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.robot.bean.MsgBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.biz.IMsgBiz;
import coms.geeknewbee.doraemon.robot.biz.IRobotBiz;
import coms.geeknewbee.doraemon.robot.view.IBindView;
import coms.geeknewbee.doraemon.robot.view.IMsgView;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2016/6/1.
 */

public class IRobotMsgPresenter {

    IMsgBiz msgBiz;

    IMsgView msgView;

    public IRobotMsgPresenter(IMsgView msgView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        msgBiz = retrofit.create(IMsgBiz.class);
        this.msgView = msgView;
    }

    /**
     * 加载消息
     */
    public void loadMsgs(){

        String token = msgView.getToken();
        String robot_pk = msgView.getRobotPk();
        int page = msgView.getPage();
        Call<HttpBean<List<MsgBean>>> call = msgBiz.getMsgs(robot_pk, token, page);
        call.enqueue(callback);
    }

    /**
     * 清空消息
     */
    public void deleteMsgs(){

        String token = msgView.getToken();
        String robot_pk = msgView.getRobotPk();
        Call<HttpBean<List<MsgBean>>> call = msgBiz.deleteMsgs(robot_pk, token);
        call.enqueue(callback);
    }

    private Callback<HttpBean<List<MsgBean>>> callback = new Callback<HttpBean<List<MsgBean>>>() {
        @Override
        public void onResponse(Response<HttpBean<List<MsgBean>>> response) {
            if(response.code() == 200){
                HttpBean<List<MsgBean>> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    msgView.setMsgBeans(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    msgView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    msgView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    msgView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    msgView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            msgView.showMessage("" + t.getMessage());
        }
    };
}
