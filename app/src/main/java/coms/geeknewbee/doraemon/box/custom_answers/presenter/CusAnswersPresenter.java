package coms.geeknewbee.doraemon.box.custom_answers.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.box.custom_answers.bean.CusAnswersBean;
import coms.geeknewbee.doraemon.box.custom_answers.biz.ICABiz;
import coms.geeknewbee.doraemon.box.custom_answers.view.ICusAnswersView;
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
 * Created by chen on 2016/3/30
 */
public class CusAnswersPresenter {
    ICABiz icaBiz;
    ICusAnswersView cusAnswersView;

    public CusAnswersPresenter(ICusAnswersView cusAnswersView) {
        this.cusAnswersView = cusAnswersView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        icaBiz = retrofit.create(ICABiz.class);
    }

    /**
     * 从服务器获取数据
     */
    public void getData() {
        String token = cusAnswersView.getToken();
        String robot_pk = cusAnswersView.getRobot_PK();
        int page = cusAnswersView.getPage();
        Call<HttpBean<List<CusAnswersBean>>> call = icaBiz.getAnswers(robot_pk, token, page);

        call.enqueue(new Callback<HttpBean<List<CusAnswersBean>>>() {
            @Override
            public void onResponse(Response<HttpBean<List<CusAnswersBean>>> response) {
                if(response.code() == 200){
                    HttpBean<List<CusAnswersBean>> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        cusAnswersView.setData(bean.getData());
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        cusAnswersView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        cusAnswersView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        cusAnswersView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        cusAnswersView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                cusAnswersView.showMessage("" + t.getMessage());
            }
        });
    }


    /**
     * 发送指定id和token删除对应对话模板
     */
    public void delete() {
        String token = cusAnswersView.getToken();
        String robot_pk = cusAnswersView.getRobot_PK();
        String ids = cusAnswersView.getIds();
        if(ids.length() == 0){
            cusAnswersView.showMessage("您还没有选择要删除的选项。");
            return;
        }
        Call<HttpBean> call = icaBiz.deleteAnswers(robot_pk, token, ids);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        cusAnswersView.deleteSuccess();
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        cusAnswersView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        cusAnswersView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        cusAnswersView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        cusAnswersView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                cusAnswersView.showMessage("" + t.getMessage());
            }
        });
    }

}
