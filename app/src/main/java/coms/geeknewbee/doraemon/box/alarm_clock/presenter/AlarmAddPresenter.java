package coms.geeknewbee.doraemon.box.alarm_clock.presenter;

import coms.geeknewbee.doraemon.box.alarm_clock.biz.IAlarmsBiz;
import coms.geeknewbee.doraemon.box.alarm_clock.view.IAlarmsAddView;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by chensiyuan on 2016/4/19.
 * Desc:
 */
public class AlarmAddPresenter {

    IAlarmsBiz alarmsBiz;
    IAlarmsAddView alarmsView;

    public AlarmAddPresenter(IAlarmsAddView alarmsView){
        this.alarmsView = alarmsView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        alarmsBiz = retrofit.create(IAlarmsBiz.class);
    }

    public void create(){
        String pk = alarmsView.getRobot_PK();
        String token = alarmsView.getToken();
        String content = alarmsView.getContent();
        String ad = alarmsView.getAlarm_Date();
        String at = alarmsView.getAlarm_Time();
        int repeat = alarmsView.getRepeat();

        if(content.length() == 0){
            alarmsView.showMessage("提醒内容不能为空。");
            return;
        }
        if(repeat == 0 && (ad == null || ad.length() == 0)){
            alarmsView.showMessage("您选择了提醒一次，所以提醒日期不能为空。");
            return;
        }
        if(at == null || at.length() == 0){
            alarmsView.showMessage("提醒时间不能为空。");
            return;
        }

        Call<HttpBean> call = alarmsBiz.addAlarm(pk, token, content, ad, at, repeat);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        alarmsView.addSuccess();
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
