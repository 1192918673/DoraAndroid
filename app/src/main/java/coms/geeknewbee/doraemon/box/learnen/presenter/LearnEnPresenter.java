package coms.geeknewbee.doraemon.box.learnen.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.box.learnen.bean.LearnEnBean;
import coms.geeknewbee.doraemon.box.learnen.biz.ILearnEnBiz;
import coms.geeknewbee.doraemon.box.learnen.view.ILearnEnView;
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
 * Created by lenovo on 2016/4/22.
 */
public class LearnEnPresenter {
    private ILearnEnBiz learnEnBiz;
    private ILearnEnView learnEnView;

    public LearnEnPresenter(ILearnEnView learnEnView){
        this.learnEnView =learnEnView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        learnEnBiz = retrofit.create(ILearnEnBiz.class);
    }

    public void retrieve(){

        String token = learnEnView.getToken();
        String robot_pk = learnEnView.getRobotID();
        int page = learnEnView.getPage();
        Call<HttpBean<List<LearnEnBean>>> call = learnEnBiz.retrieve(robot_pk, token, page);

        call.enqueue(new Callback<HttpBean<List<LearnEnBean>>>() {
            @Override
            public void onResponse(Response<HttpBean<List<LearnEnBean>>> response) {
                if(response.code() == 200){
                    HttpBean<List<LearnEnBean>> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        learnEnView.transfer(bean.getData());
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        learnEnView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        learnEnView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        learnEnView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        learnEnView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                learnEnView.showMessage("" + t.getMessage());
            }
        });
    }
}
