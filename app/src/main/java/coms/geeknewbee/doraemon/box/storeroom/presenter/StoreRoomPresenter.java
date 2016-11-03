package coms.geeknewbee.doraemon.box.storeroom.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.box.storeroom.bean.StoreRoomBean;
import coms.geeknewbee.doraemon.box.storeroom.biz.IStoreRoomBiz;
import coms.geeknewbee.doraemon.box.storeroom.view.IStoreRoomView;
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
 * Created by chen on 2016/4/11
 */
public class StoreRoomPresenter {
    private IStoreRoomBiz storeRoomBiz;
    private IStoreRoomView storeRoomView;

    public StoreRoomPresenter(IStoreRoomView storeRoomView){
        this.storeRoomView = storeRoomView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        storeRoomBiz = retrofit.create(IStoreRoomBiz.class);
    }

    /**
     * 从服务器获取数据
     */
    public void getData() {
        String token = storeRoomView.getToken();
        String robot_pk = storeRoomView.getRobot_PK();
        int page = storeRoomView.getPage();
        Call<HttpBean<List<StoreRoomBean>>> call = storeRoomBiz.getGoods(robot_pk, token, page);

        call.enqueue(new Callback<HttpBean<List<StoreRoomBean>>>() {
            @Override
            public void onResponse(Response<HttpBean<List<StoreRoomBean>>> response) {
                if(response.code() == 200){
                    HttpBean<List<StoreRoomBean>> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        storeRoomView.setData(bean.getData());
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        storeRoomView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        storeRoomView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        storeRoomView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        storeRoomView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                storeRoomView.showMessage("" + t.getMessage());
            }
        });
    }


    /**
     * 发送指定id和token删除对应对话模板
     */
    public void delete() {
        String token = storeRoomView.getToken();
        String robot_pk = storeRoomView.getRobot_PK();
        String ids = storeRoomView.getIds();
        if(ids.length() == 0){
            storeRoomView.showMessage("您还没有选择要删除的选项。");
            return;
        }
        Call<HttpBean> call = storeRoomBiz.deleteGoods(robot_pk, token, ids);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        storeRoomView.deleteSuccess();
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        storeRoomView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        storeRoomView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        storeRoomView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        storeRoomView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                storeRoomView.showMessage("" + t.getMessage());
            }
        });
    }
}
