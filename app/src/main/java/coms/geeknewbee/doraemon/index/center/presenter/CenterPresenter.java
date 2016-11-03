package coms.geeknewbee.doraemon.index.center.presenter;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.index.center.bean.APPBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.index.center.biz.ICenterBiz;
import coms.geeknewbee.doraemon.index.center.view.ICenterView;
import coms.geeknewbee.doraemon.utils.FileHandler;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by chen on 2016/4/12
 */
public class CenterPresenter {
    private ICenterView centerView;
    private ICenterBiz centerBiz;

    public CenterPresenter(ICenterView centerView){
        this.centerView = centerView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        centerBiz = retrofit.create(ICenterBiz.class);
    }

    public void getUser(){
        centerView.showLoading();

        String token = centerView.getToken();
        Call<HttpBean<UserBean>> call = centerBiz.getUser(token);
        call.enqueue(userCallback);
    }

    public void getAppVersion(){
        Call<HttpBean<APPBean>> call = centerBiz.getAppVersion();
        call.enqueue(appCallback);
    }

    public void download(String url){
        String domain = "http://doraemon.microfastup.com:80/";
        String domain2 = "http://doraemon.microfastup.com/";
        if(url.contains(domain)){
            url = url.substring(domain.length());
        } else if(url.contains(domain2)){
            url = url.substring(domain2.length());
        }
        Call<ResponseBody> call = centerBiz.getFile(url);
        call.enqueue(fileCallback);
    }

    private Callback<HttpBean<UserBean>> userCallback = new Callback<HttpBean<UserBean>>() {
        @Override
        public void onResponse(Response<HttpBean<UserBean>> response) {
            centerView.hideLoading();
            if(response.code() == 200){
                HttpBean<UserBean> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    centerView.setData(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    centerView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    Toast.makeText(MyApplication.getContext(),"" + msg,Toast.LENGTH_SHORT).show();
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    Toast.makeText(MyApplication.getContext(),"" + response.errorBody().string(),Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    Toast.makeText(MyApplication.getContext(),"" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            centerView.hideLoading();
            Toast.makeText(MyApplication.getContext(),"" + t.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };

    private Callback<HttpBean<APPBean>> appCallback = new Callback<HttpBean<APPBean>>() {
        @Override
        public void onResponse(Response<HttpBean<APPBean>> response) {
            if(response.code() == 200){
                HttpBean<APPBean> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    centerView.setAPP(bean.getData());
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    centerView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    centerView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    centerView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            centerView.showMessage("" + t.getMessage());
        }
    };

    private Callback<ResponseBody> fileCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Response<ResponseBody> response) {
            if(response.code() == 200){
                try {
                    FileHandler fh = new FileHandler(MyApplication.getContext());
                    File temp = new File(fh.SDPATH + fh.Path + "/apk/");
                    if(!temp.exists()){
                        temp.mkdirs();
                    }

                    InputStream is = response.body().byteStream();
                    File file = new File(fh.getStoreName("apk", ".apk"));
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                    fos.close();
                    bis.close();
                    is.close();

                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    MyApplication.getContext().startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    centerView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    centerView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            centerView.showMessage("" + t.getMessage());
        }
    };
}
