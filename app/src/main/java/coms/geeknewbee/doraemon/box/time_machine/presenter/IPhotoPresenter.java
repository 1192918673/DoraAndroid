package coms.geeknewbee.doraemon.box.time_machine.presenter;

import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.box.time_machine.biz.IPhotoBiz;
import coms.geeknewbee.doraemon.box.time_machine.view.IPhotoView;
import coms.geeknewbee.doraemon.entity.RobotPhoto;
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
 * Created by Administrator on 2016/6/17.
 */
public class IPhotoPresenter {

    IPhotoBiz photoBiz;

    IPhotoView photoView;

    public IPhotoPresenter(IPhotoView photoView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        photoBiz = retrofit.create(IPhotoBiz.class);
        this.photoView = photoView;
    }

    /**
     * 查询相册
     */
    public void getPhotos(){
        String token = photoView.getToken();
        String robot_pk = photoView.getRobotPk();
        int page = photoView.getPage();
        Call<HttpBean<Map<String, List<RobotPhoto>>>> call =
                photoBiz.getPhotos(robot_pk, token, page);
        call.enqueue(callback);
    }

    /**
     * 移除照片
     */
    public void removePhoto(){
        String token = photoView.getToken();
        String robot_pk = photoView.getRobotPk();
        String ids = photoView.getIds();
        if(ids == null || ids.length() == 0){
            photoView.showMessage("请先选择您要删除的选项。");
            return;
        }
        Call<HttpBean> call = photoBiz.removePhotos(robot_pk, token, ids);
        call.enqueue(optionCallback);
    }

    private Callback<HttpBean<Map<String, List<RobotPhoto>>>> callback =
            new Callback<HttpBean<Map<String, List<RobotPhoto>>>>() {
        @Override
        public void onResponse(Response<HttpBean<Map<String, List<RobotPhoto>>>> response) {
            if(response.code() == 200){
                HttpBean<Map<String, List<RobotPhoto>>> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + response.toString());
                    ILog.e("Http", "" + bean.getData().toString());
                    photoView.setData(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    photoView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    photoView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    photoView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    photoView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            photoView.showMessage("" + t.getMessage());
        }
    };

    private Callback<HttpBean> optionCallback = new Callback<HttpBean>() {
        @Override
        public void onResponse(Response<HttpBean> response) {
            if(response.code() == 200){
                HttpBean bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + response.toString());
                    photoView.deleteSuccess();
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    photoView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    photoView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    photoView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    photoView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            photoView.showMessage("" + t.getMessage());
        }
    };
}
