package coms.geeknewbee.doraemon.index.center.biz;

import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.APPBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by chen on 2016/4/12
 */
public interface ICenterBiz {
    /**
     * 查询自己的信息
     * @param token
     * @return
     */
    @GET("/auth/user")
    public Call<HttpBean<UserBean>> getUser(@Query("token") String token);

    /**
     * 查询应用版本信息
     * @return
     */
    @GET("/app/version")
    public Call<HttpBean<APPBean>> getAppVersion();

    /**
     * 下载文件
     * @param fileName
     * @return
     */
    @GET("/{fileName}")
    @Headers({"Content-Type: application/vnd.android.package-archive"})
    Call<ResponseBody> getFile(@Path("fileName") String fileName);
}
