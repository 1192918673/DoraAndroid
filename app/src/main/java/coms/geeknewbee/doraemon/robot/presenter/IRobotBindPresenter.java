package coms.geeknewbee.doraemon.robot.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.biz.IRobotBiz;
import coms.geeknewbee.doraemon.robot.view.IBindView;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import coms.geeknewbee.doraemon.utils.ToastTool;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2016/6/1.
 */

public class IRobotBindPresenter {

    IRobotBiz robotBiz;

    IBindView bindView;

    public IRobotBindPresenter(IBindView bindView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        robotBiz = retrofit.create(IRobotBiz.class);
        this.bindView = bindView;
    }
    public void bindRobot(){

        String token = bindView.getToken();
        String name = "哆啦A梦";
        int volume = 85;
        String serial_no = bindView.getSerialNo();
        Call<HttpBean<RobotBean>> call = robotBiz.addRobot(token, name, volume, serial_no);
        call.enqueue(new Callback<HttpBean<RobotBean>>() {
            @Override
            public void onResponse(Response<HttpBean<RobotBean>> response) {
                if(response.code() == 200){
                    HttpBean<RobotBean> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        //绑定成功
                        bindView.bindSuccess(bean.getData());

                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        bindView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        bindView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        bindView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        bindView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                bindView.showMessage("" + t.getMessage());
            }
        });
    }
}
