package coms.geeknewbee.doraemon.index.center.presenter;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.index.center.biz.IEditProfileBiz;
import coms.geeknewbee.doraemon.index.center.view.IEditProfileView;
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
 * Created by chen on 2016/4/12
 */
public class EditProfilePresenter {

    private IEditProfileBiz editProfileBiz;
    private IEditProfileView editProfileView;

    public EditProfilePresenter(IEditProfileView editProfileView){
        this.editProfileView = editProfileView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        editProfileBiz = retrofit.create(IEditProfileBiz.class);
    }

    //进入修改页，读取数据以供显示
    public void getUser(){
        editProfileView.showLoading();

        Call<HttpBean<UserBean>> call = editProfileBiz.getUser(editProfileView.getToken());
        call.enqueue(new Callback<HttpBean<UserBean>>() {
            @Override
            public void onResponse(Response<HttpBean<UserBean>> response) {
                if(response.code() == 200){
                    HttpBean<UserBean> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        editProfileView.setData(bean.getData());
                        editProfileView.hideLoading();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        Toast.makeText(MyApplication.getContext(),
                                "" + msg,Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        Toast.makeText(MyApplication.getContext(),
                                "" + response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        Toast.makeText(MyApplication.getContext(),
                                "" + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                editProfileView.hideLoading();
                Toast.makeText(MyApplication.getContext(),
                        "" + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //点击确定，上传数据
    public void createUser(){
        editProfileView.showLoading();

        Map<String, RequestBody> params = new HashMap<String, RequestBody>();
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), editProfileView.getToken());
        params.put("token", token);
        if(!StringHandler.isEmpty(editProfileView.getNickName())){
            RequestBody nickname = RequestBody.create(MediaType.parse("text/plain"), editProfileView.getNickName());
            params.put("nickname", nickname);
        }
        if(!StringHandler.isEmpty(editProfileView.getGender())){
            RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), editProfileView.getGender());
            params.put("gender", gender);
        }
        if(!StringHandler.isEmpty(editProfileView.getBirth())){
            ILog.e("" + editProfileView.getBirth());
            RequestBody birthday = RequestBody.create(MediaType.parse("text/plain"), editProfileView.getBirth());
            params.put("birthday", birthday);
        }
        if(editProfileView.getAvatar() != null){
            RequestBody avatar = RequestBody.create(MediaType.parse("image/*"), editProfileView.getAvatar());
            params.put("avatar\"; filename=\"" + editProfileView.getAvatar().getName(), avatar);
        }

        Call<HttpBean<UserBean>> call = editProfileBiz.createUser(params);
        call.enqueue(new Callback<HttpBean<UserBean>>() {
            @Override
            public void onResponse(Response<HttpBean<UserBean>> response) {
                if(response.code() == 200){
                    HttpBean<UserBean> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        editProfileView.setData(bean.getData());
                        editProfileView.updateUser(bean.getData());
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        Toast.makeText(MyApplication.getContext(),
                                "" + msg,Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        Toast.makeText(MyApplication.getContext(),
                                "" + response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        Toast.makeText(MyApplication.getContext(),
                                "" + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                editProfileView.hideLoading();
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                editProfileView.hideLoading();
                Toast.makeText(MyApplication.getContext(),
                        "" + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
