package coms.geeknewbee.doraemon.index.center.presenter;

import android.os.Handler;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.index.center.biz.IMobileBiz;
import coms.geeknewbee.doraemon.index.center.view.IEditMobileView;
import coms.geeknewbee.doraemon.register_login.bean.GetTokenBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by chen on 2016/4/7
 */
public class EditMobilePresenter {
    private IEditMobileView editMobileView;
    private IMobileBiz editMobileBiz;
    private Handler mHandler = new Handler();

    public EditMobilePresenter(IEditMobileView editMobileView){
        this.editMobileView = editMobileView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        editMobileBiz = retrofit.create(IMobileBiz.class);
    }

    public void getCode(){
        String mobile = editMobileView.getMobile();
        if(!StringHandler.testPhone(mobile)){
            editMobileView.showMessage("请输入正确的手机号！");
            return;
        }
        Call<HttpBean> call = editMobileBiz.sendSMS(mobile, "" + editMobileView.getStatus());
        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        editMobileView.sendSuccess();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        editMobileView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        editMobileView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        editMobileView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                editMobileView.showMessage("" + t.getMessage());
            }
        });
    }

    public void checkCode(){
        String mobile = editMobileView.getMobile();
        String code = editMobileView.getCode();
        if(!StringHandler.testPhone(mobile)){
            editMobileView.showMessage("请输入正确的手机号！");
            return;
        }
        if(StringHandler.isEmpty(code)){
            editMobileView.showMessage("验证码不能为空！");
            return;
        }
        Call<HttpBean<GetTokenBean>> call = editMobileBiz.checkSMS(mobile, code, mobile, "1");
        call.enqueue(new Callback<HttpBean<GetTokenBean>>() {
            @Override
            public void onResponse(Response<HttpBean<GetTokenBean>> response) {
                if(response.code() == 200){
                    HttpBean<GetTokenBean> bean = response.body();
                    if(bean.getCode() == 200){
                        editMobileView.checkSuccess(bean.getData().getToken());
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        editMobileView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        editMobileView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        editMobileView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                editMobileView.showMessage("" + t.getMessage());
            }
        });
    }

    //点击确定，上传数据
    public void updateMoblie(){
        String mobile = editMobileView.getMobile();
        String code = editMobileView.getCode();
        if(!StringHandler.testPhone(mobile)){
            editMobileView.showMessage("请输入正确的手机号！");
            return;
        }
        if(StringHandler.isEmpty(code)){
            editMobileView.showMessage("验证码不能为空！");
            return;
        }

        Map<String, RequestBody> params = new HashMap<String, RequestBody>();
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), editMobileView.getToken());
        params.put("token", token);

        RequestBody m = RequestBody.create(MediaType.parse("text/plain"), mobile);
        params.put("mobile", m);

        RequestBody sms_code = RequestBody.create(MediaType.parse("text/plain"), code);
        params.put("sms_code", sms_code);

        Call<HttpBean<UserBean>> call = editMobileBiz.updateUser(params);
        call.enqueue(new Callback<HttpBean<UserBean>>() {
            @Override
            public void onResponse(Response<HttpBean<UserBean>> response) {
                if(response.code() == 200){
                    HttpBean<UserBean> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        editMobileView.checkSuccess(null);
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        editMobileView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        editMobileView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        editMobileView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                editMobileView.showMessage("" + t.getMessage());
            }
        });
    }
}
