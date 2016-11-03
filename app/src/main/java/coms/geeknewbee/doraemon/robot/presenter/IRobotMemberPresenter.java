package coms.geeknewbee.doraemon.robot.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.biz.IMemberBiz;
import coms.geeknewbee.doraemon.robot.view.IMemberView;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2016/6/1.
 */

public class IRobotMemberPresenter {

    IMemberBiz memberBiz;

    IMemberView memberView;

    /**
     * 0，邀请，1，设为管理员，2，移除
     */
    int type;

    String pk;

    public IRobotMemberPresenter(IMemberView memberView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        memberBiz = retrofit.create(IMemberBiz.class);
        this.memberView = memberView;
    }

    /**
     * 查询用户信息
     */
    public void getUser(){
        String token = memberView.getToken();
        Call<HttpBean<UserBean>> call = memberBiz.getUser(token);
        call.enqueue(userCallback);
    }

    /**
     * 查询成员
     */
    public void getMembers(){
        String token = memberView.getToken();
        String robot_pk = memberView.getRobotPk();
        Call<HttpBean<List<UserBean>>> call = memberBiz.getMembers(robot_pk, token);
        call.enqueue(callback);
    }

    /**
     * 邀请成员
     */
    public void inviteMember(){
        String token = memberView.getToken();
        String robot_pk = memberView.getRobotPk();
        String mobile = memberView.getMobile();
        // ILog.e("Http", token + " - " + robot_pk + " - " + mobile);
        if(mobile == null || !StringHandler.testPhone(mobile)){
            memberView.showMessage("请输入正确的手机号码！");
            return;
        }
        Call<HttpBean> call = memberBiz.inviteMember(robot_pk, token, mobile);
        type = 0;
        call.enqueue(optionCallback);
    }

    /**
     * 设置管理员
     */
    public void optionMember(int position){
        String token = memberView.getToken();
        String robot_pk = memberView.getRobotPk();
        String pk = memberView.getUserPk(position);
        String role = memberView.getRole(position);
        // ILog.e("Http", token + " - " + robot_pk + " - " + pk + " - " + role);
        Call<HttpBean> call = memberBiz.optionMember(robot_pk, pk, token, role);
        type = 1;
        this.pk = pk;
        call.enqueue(optionCallback);
    }

    /**
     * 移除成员
     */
    public void removeMember(int position){
        String token = memberView.getToken();
        String robot_pk = memberView.getRobotPk();
        String pk = memberView.getUserPk(position);
        // ILog.e("Http", token + " - " + robot_pk + " - " + pk);
        Call<HttpBean> call = memberBiz.removeMember(robot_pk, pk, token);
        type = 2;
        this.pk = pk;
        call.enqueue(optionCallback);
    }

    private Callback<HttpBean<List<UserBean>>> callback = new Callback<HttpBean<List<UserBean>>>() {
        @Override
        public void onResponse(Response<HttpBean<List<UserBean>>> response) {
            if(response.code() == 200){
                HttpBean<List<UserBean>> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + response.toString());
                    ILog.e("Http", "" + bean.getData().toString());
                    memberView.setUserBeans(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    memberView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    memberView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    memberView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    memberView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            memberView.showMessage("" + t.getMessage());
        }
    };

    private Callback<HttpBean> optionCallback = new Callback<HttpBean>() {
        @Override
        public void onResponse(Response<HttpBean> response) {
            if(response.code() == 200){
                HttpBean bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + response.toString());
                    ILog.e("Http", "" + bean.getData().toString());
                    memberView.optionSuccess(type, pk);
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    memberView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    memberView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    memberView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    memberView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            memberView.showMessage("" + t.getMessage());
        }
    };

    private Callback<HttpBean<UserBean>> userCallback = new Callback<HttpBean<UserBean>>() {
        @Override
        public void onResponse(Response<HttpBean<UserBean>> response) {
            if(response.code() == 200){
                HttpBean<UserBean> bean = response.body();
                if(bean.getCode() == 200){
                    ILog.e("Http", "" + bean.getData().toString());
                    memberView.setUser(bean.getData());
                } else if(bean.getCode() == 403 && bean.getMsg() != null
                        && bean.getMsg().contains("Invalid token")){
                    // 登录超时
                    memberView.loginTimeout();
                } else {
                    String msg = StringHandler
                            .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                    ILog.e("Http", msg);
                    memberView.showMessage("" + msg);
                }
            } else {
                try{
                    ILog.e("Http", "" + response.errorBody().string());
                    memberView.showMessage("" + response.errorBody().string());
                } catch (Exception e){
                    ILog.e("Http", "" + e.getMessage());
                    ILog.e(e);
                    memberView.showMessage("" + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            ILog.e("Http", "" + t.getMessage());
            ILog.e(t);
            memberView.showMessage("" + t.getMessage());
        }
    };
}
