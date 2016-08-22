package coms.geeknewbee.doraemon.box.facetime.presenter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import coms.geeknewbee.doraemon.box.facetime.biz.IUserBiz;
import coms.geeknewbee.doraemon.box.facetime.view.IUserView;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
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
 * Created by Administrator on 2016/6/1.
 */
public class IUserPresenter {

    IUserBiz userBiz;

    IUserView userView;

    /**
     * 0，邀请，1，设为管理员，2，移除
     */
    int type;

    String pk;

    public IUserPresenter(IUserView userView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userBiz = retrofit.create(IUserBiz.class);
        this.userView = userView;
    }

    /**
     * 查询用户信息
     */
    public void getUser(){
        String token = userView.getToken();
        Call<HttpBean<UserBean>> call = userBiz.getUser(token);
        call.enqueue(userCallback);
    }

    public void uploadImg(){
        String token = userView.getToken();
        File photo = userView.getPhoto();
        String pk = userView.getRobotPk();

        Map<String, RequestBody> params = new HashMap<String, RequestBody>();
        RequestBody t = RequestBody.create(MediaType.parse("text/plain"), token);
        params.put("token", t);

        RequestBody img = RequestBody.create(MediaType.parse("image/*"), photo);
        params.put("photo\"; filename=\""+photo.getName(), img);
        for (String key:params.keySet()) {
            ILog.e("----------------["+key+"]----------------");
        }
        Call<HttpBean> call = userBiz.uploadImage(pk, params);

        call.enqueue(imgCallback);
    }

    private Callback<HttpBean<UserBean>> userCallback = new Callback<HttpBean<UserBean>>() {
        @Override
        public void onResponse(Response<HttpBean<UserBean>> response) {
            if(response.code() == 200){
                HttpBean<UserBean> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    userView.setUser(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    userView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    userView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    userView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    userView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            userView.showMessage("" + t.getMessage());
        }
    };

    private Callback<HttpBean> imgCallback = new Callback<HttpBean>() {
        @Override
        public void onResponse(Response<HttpBean> response) {
            if(response.code() == 200){
                HttpBean bean = response.body();
                if(bean.getCode() == 200){
                    userView.showMessage("拍照保存成功！");
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    userView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    userView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    userView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    userView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            userView.showMessage("" + t.getMessage());
        }
    };
}
