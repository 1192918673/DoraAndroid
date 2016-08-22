package coms.geeknewbee.doraemon.box.custom_answers.presenter;

import coms.geeknewbee.doraemon.box.custom_answers.biz.ICABiz;
import coms.geeknewbee.doraemon.box.custom_answers.view.ICusAnswerAddView;
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
 * Created by chen on 2016/6/13
 */
public class CusAnswersAddPresenter {
    ICABiz icaBiz;
    ICusAnswerAddView cusAnswerAddView;

    public CusAnswersAddPresenter(ICusAnswerAddView cusAnswerAddView){
        this.cusAnswerAddView = cusAnswerAddView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        icaBiz = retrofit.create(ICABiz.class);
    }

    public void create(){
        String pk = cusAnswerAddView.getRobot_PK();
        String token = cusAnswerAddView.getToken();
        String question = cusAnswerAddView.getQuestion();
        String answer = cusAnswerAddView.getAnswer();

        if(question.length() == 0){
            cusAnswerAddView.showMessage("问题内容不能为空。");
            return;
        }
        if(answer.length() == 0){
            cusAnswerAddView.showMessage("答案内容不能为空。");
            return;
        }

        Call<HttpBean> call = icaBiz.addAnswer(pk, token, question, answer);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        cusAnswerAddView.showSuccess();
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        cusAnswerAddView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        cusAnswerAddView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        cusAnswerAddView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        cusAnswerAddView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                cusAnswerAddView.showMessage("" + t.getMessage());
            }
        });
    }

}
