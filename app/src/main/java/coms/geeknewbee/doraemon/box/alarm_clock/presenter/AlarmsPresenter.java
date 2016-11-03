package coms.geeknewbee.doraemon.box.alarm_clock.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.box.alarm_clock.bean.AlarmsBean;
import coms.geeknewbee.doraemon.box.alarm_clock.biz.IAlarmsBiz;
import coms.geeknewbee.doraemon.box.alarm_clock.view.IAlarmsView;
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
 * Created by chen on 2016/4/15
 */
public class AlarmsPresenter {

    private IAlarmsView alarmsView;
    private IAlarmsBiz alarmsBiz;

    public AlarmsPresenter(IAlarmsView alarmsView) {
        this.alarmsView = alarmsView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        alarmsBiz = retrofit.create(IAlarmsBiz.class);
    }

    /**
     * 从服务器获取数据
     */
    public void getData() {
        String token = alarmsView.getToken();
        String robot_pk = alarmsView.getRobotPK();
        int page = alarmsView.getPage();
        Call<HttpBean<List<AlarmsBean>>> call = alarmsBiz.getAlarms(robot_pk, token, page);

        call.enqueue(new Callback<HttpBean<List<AlarmsBean>>>() {
            @Override
            public void onResponse(Response<HttpBean<List<AlarmsBean>>> response) {
                if(response.code() == 200){
                    HttpBean<List<AlarmsBean>> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        alarmsView.transfer(bean.getData());
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        alarmsView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        alarmsView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        alarmsView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        alarmsView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                alarmsView.showMessage("" + t.getMessage());
            }
        });
    }


    /**
     * 发送指定id和token删除对应对话模板
     */
    public void delete() {
        String token = alarmsView.getToken();
        String robot_pk = alarmsView.getRobotPK();
        String ids = alarmsView.getIds();
        if(ids.length() == 0){
            alarmsView.showMessage("您还没有选择要删除的选项。");
            return;
        }
        Call<HttpBean> call = alarmsBiz.deleteAlarms(robot_pk, token, ids);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        alarmsView.deleteSuccess();
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        alarmsView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        alarmsView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        alarmsView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        alarmsView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                alarmsView.showMessage("" + t.getMessage());
            }
        });
    }
}
