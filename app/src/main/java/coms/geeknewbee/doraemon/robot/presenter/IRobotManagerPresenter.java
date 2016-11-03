package coms.geeknewbee.doraemon.robot.presenter;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.biz.IRobotBiz;
import coms.geeknewbee.doraemon.robot.view.IManagerView;
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

public class IRobotManagerPresenter {

    IRobotBiz robotBiz;

    IManagerView managerView;

    String name;

    int vol;

    public IRobotManagerPresenter(IManagerView managerView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        robotBiz = retrofit.create(IRobotBiz.class);
        this.managerView = managerView;
    }

    /**
     * 修改机器人信息
     */
    public void modifyRobot(){
        name = managerView.getName();
        vol = managerView.getVolume();
        RobotBean robot = managerView.getRobot();
        if(robot == null || (name.equals(robot.getName())
            && vol == robot.getVolume()) || name.length() == 0){
            return;
        }
        String token = managerView.getToken();
        String robot_pk = managerView.getRobotPk();
        Call<HttpBean<RobotBean>> call = robotBiz.modifyRobot(robot_pk, token, name, vol);
        call.enqueue(callback);
    }

    public void removeRobot(){
        name = null;
        String token = managerView.getToken();
        String robot_pk = managerView.getRobotPk();
        Call<HttpBean<RobotBean>> call = robotBiz.removeRobot(robot_pk, token);
        call.enqueue(callback);
    }

    private Callback<HttpBean<RobotBean>> callback = new Callback<HttpBean<RobotBean>>() {
        @Override
        public void onResponse(Response<HttpBean<RobotBean>> response) {
            if(response.code() == 200){
                HttpBean<RobotBean> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    managerView.setRobotBean(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    managerView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    managerView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    managerView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    managerView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            managerView.showMessage("" + t.getMessage());
        }
    };

    public String getName() {
        return name;
    }

    public int getVol() {
        return vol;
    }
}
