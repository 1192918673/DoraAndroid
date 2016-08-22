package coms.geeknewbee.doraemon.index.center.biz;

import java.io.File;
import java.util.Map;

import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by chen on 2016/4/12
 */
public interface IEditProfileBiz {

    /**
     * 修改资料页，
     * 进入时，从服务器读取;
     * 点击确定时，上传服务器;
     * 返回时，判断是否有资料修改，若有修改，上传;若没有,直接返回。(如何判断)
     */
    @GET("user")
    public Call<HttpBean<UserBean>> getUser(@Query("token") String token);

    //修改用户信息
    @Multipart
    @PATCH("user")
    public Call<HttpBean<UserBean>> createUser(@PartMap Map<String, RequestBody> params);

}
