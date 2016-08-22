package coms.geeknewbee.doraemon.box.facetime.biz;


import java.util.Map;

import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/1.
 */
public interface IUserBiz {

    /**
     * 查询自己的信息
     * @param token
     * @return
     */
    @GET("/auth/user")
    public Call<HttpBean<UserBean>> getUser(@Query("token") String token);

    /**
     * 上传图片
     * @param params
     * @return
     */
    @Multipart
    @POST("/robots/{robot_pk}/photo")
    Call<HttpBean> uploadImage(@Path("robot_pk") String robot_pk,
                               @PartMap Map<String, RequestBody> params);
}
