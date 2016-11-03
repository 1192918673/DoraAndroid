package coms.geeknewbee.doraemon.index.center.biz;

import java.util.Map;

import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.register_login.bean.GetTokenBean;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/12.
 */
public interface IMobileBiz {

    /**
     * 发送短信
     * @param mobile
     * @param type
     * @return
     */
    @GET("/auth/sms")
    public Call<HttpBean> sendSMS(@Query("mobile") String mobile, @Query("type") String type);

    /**
     * 验证短信
     * @param mobile
     * @param sms_code
     * @param cid
     * @param client_type
     * @return
     */
    @GET("/auth/token")
    public Call<HttpBean<GetTokenBean>> checkSMS(@Query("mobile") String mobile, @Query("sms_code") String sms_code,
                                                 @Query("cid") String cid, @Query("client_type") String client_type);

    //修改用户信息
    @Multipart
    @PATCH("/auth/user")
    public Call<HttpBean<UserBean>> updateUser(@PartMap Map<String, RequestBody> params);
}
