package coms.geeknewbee.doraemon.box.smart_home.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.box.smart_home.bean.SmartBean;
import coms.geeknewbee.doraemon.box.smart_home.biz.ISmartBiz;
import coms.geeknewbee.doraemon.box.smart_home.view.ISmartView;
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
 * Created by Administrator on 2016/6/23.
 */
public class ISmartPresenter {

    ISmartBiz smartBiz;

    ISmartView smartView;

    public ISmartPresenter(ISmartView smartView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        smartBiz = retrofit.create(ISmartBiz.class);
        this.smartView = smartView;
    }

    public void getSmarts(){
        String token = smartView.getToken();
        int page = smartView.getPage();
        Call<HttpBean<List<SmartBean>>> call = smartBiz.getSmarts(token, page);
        call.enqueue(callback);
    }

    public void addInstructions(){
        String token = smartView.getToken();
        String robot_pk = smartView.getRobotPk();
        int baseId = smartView.getBaseId();
        String data = smartView.getData();
        String bd = baseId + "";
        if(baseId == 0){
            bd = "";
        }
        Call<HttpBean> call = smartBiz.addInstructions(robot_pk, token, bd, data);
        call.enqueue(addCallback);
    }

    public void getInstructions(){
        String pk = smartView.getRobotPk();
        String token = smartView.getToken();
        Call<HttpBean<List<SmartBean>>> call = smartBiz.getInstructions(pk, token);
        call.enqueue(callback);
    }

    private Callback<HttpBean<List<SmartBean>>> callback = new Callback<HttpBean<List<SmartBean>>>() {
        @Override
        public void onResponse(Response<HttpBean<List<SmartBean>>> response) {
            if(response.code() == 200){
                HttpBean<List<SmartBean>> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    smartView.setSmartBeans(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    smartView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    // smartView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    // smartView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    // smartView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            // smartView.showMessage("" + t.getMessage());
        }
    };

    private Callback<HttpBean> addCallback = new Callback<HttpBean>() {
        @Override
        public void onResponse(Response<HttpBean> response) {
            if(response.code() == 200){
                HttpBean<List<SmartBean>> bean = response.body();
                if(bean.getCode() == 200){
                    smartView.addSuccess();
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    smartView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    smartView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    smartView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    smartView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            smartView.showMessage("" + t.getMessage());
        }
    };
}
