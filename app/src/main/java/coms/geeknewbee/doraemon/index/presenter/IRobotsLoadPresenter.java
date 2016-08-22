package coms.geeknewbee.doraemon.index.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.index.view.IIndexView;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.biz.IRobotBiz;
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

public class IRobotsLoadPresenter {

    IRobotBiz robotBiz;

    IIndexView indexView;

    public IRobotsLoadPresenter(IIndexView indexView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        robotBiz = retrofit.create(IRobotBiz.class);
        this.indexView = indexView;
    }

    /**
     * 查询用户信息
     */
    public void getUser(){
        String token = indexView.getToken();
        Call<HttpBean<UserBean>> call = robotBiz.getUser(token);
        call.enqueue(userCallback);
    }

    public void getRobot(){
        String token = indexView.getToken();
        Call<HttpBean<List<RobotBean>>> call = robotBiz.getRobots(token);
        call.enqueue(new Callback<HttpBean<List<RobotBean>>>() {
            @Override
            public void onResponse(Response<HttpBean<List<RobotBean>>> response) {
                if(response.code() == 200){
                    HttpBean<List<RobotBean>> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        ILog.e("Http", "" + response.toString());
                        indexView.setData(bean.getData());
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        indexView.loginTimeout();
                    } else {
                        String msg = bean.getMsg();
                        ILog.e("Http", msg);
                        indexView.showMessage(msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        indexView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        indexView.showMessage("" + e.getMessage());
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                indexView.showMessage("" + t.getMessage());
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
            }
        });
    }

    private Callback<HttpBean<UserBean>> userCallback = new Callback<HttpBean<UserBean>>() {
        @Override
        public void onResponse(Response<HttpBean<UserBean>> response) {
            if(response.code() == 200){
                HttpBean<UserBean> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    indexView.setData(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    indexView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    indexView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    indexView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    indexView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            indexView.showMessage("" + t.getMessage());
        }
    };
}
