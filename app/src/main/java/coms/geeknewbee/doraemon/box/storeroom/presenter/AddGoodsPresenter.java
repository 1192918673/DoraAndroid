package coms.geeknewbee.doraemon.box.storeroom.presenter;

import coms.geeknewbee.doraemon.box.storeroom.biz.IStoreRoomBiz;
import coms.geeknewbee.doraemon.box.storeroom.view.IAddGoodsView;
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
 * Created by lenovo on 2016/4/21.
 */
public class AddGoodsPresenter {
    IAddGoodsView addGoodsView;
    IStoreRoomBiz storeRoomBiz;

    public AddGoodsPresenter(IAddGoodsView addGoodsView){
        this.addGoodsView = addGoodsView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        storeRoomBiz = retrofit.create(IStoreRoomBiz.class);
    }

    /**
     * 添加
     */
    public void create(){
        String pk = addGoodsView.getRobot_PK();
        String token = addGoodsView.getToken();
        String name = addGoodsView.getName();
        String place = addGoodsView.getPlace();

        if(name.length() == 0){
            addGoodsView.showMessage("物品名称不能为空。");
            return;
        }
        if(name.length() > 30){
            addGoodsView.showMessage("物品名称不能超过30字。");
            return;
        }
        if(place.length() == 0){
            addGoodsView.showMessage("存放地点不能为空。");
            return;
        }
        if(place.length() > 30){
            addGoodsView.showMessage("存放地点不能超过30字。");
            return;
        }

        Call<HttpBean> call = storeRoomBiz.addGood(pk, token, name, place);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        addGoodsView.showSuccess();
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        addGoodsView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        addGoodsView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        addGoodsView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        addGoodsView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                addGoodsView.showMessage("" + t.getMessage());
            }
        });
    }
}
