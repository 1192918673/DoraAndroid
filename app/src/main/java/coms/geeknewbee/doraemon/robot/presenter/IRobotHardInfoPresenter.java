package coms.geeknewbee.doraemon.robot.presenter;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.robot.bean.HardInfoBean;
import coms.geeknewbee.doraemon.robot.biz.IRobotBiz;
import coms.geeknewbee.doraemon.robot.view.IHardInfoView;
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

public class IRobotHardInfoPresenter {

    IRobotBiz robotBiz;

    IHardInfoView hardInfoView;

    public IRobotHardInfoPresenter(IHardInfoView hardInfoView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        robotBiz = retrofit.create(IRobotBiz.class);
        this.hardInfoView = hardInfoView;
    }

    /**
     * 修改机器人信息
     */
    public void getHardInfo(){
        String token = hardInfoView.getToken();
        String robot_pk = hardInfoView.getRobotPk();
        Call<HttpBean<HardInfoBean>> call = robotBiz.getHardInfo(robot_pk, token);
        call.enqueue(callback);
    }

    private Callback<HttpBean<HardInfoBean>> callback = new Callback<HttpBean<HardInfoBean>>() {
        @Override
        public void onResponse(Response<HttpBean<HardInfoBean>> response) {
            if(response.code() == 200){
                HttpBean<HardInfoBean> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    hardInfoView.setHardInfo(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    hardInfoView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    hardInfoView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    hardInfoView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    hardInfoView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            hardInfoView.showMessage("" + t.getMessage());
        }
    };
}
