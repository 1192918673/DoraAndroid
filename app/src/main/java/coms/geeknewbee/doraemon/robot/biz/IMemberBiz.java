package coms.geeknewbee.doraemon.robot.biz;

import java.util.List;

import coms.geeknewbee.doraemon.global.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/1.
 */
public interface IMemberBiz {

    /**
     * 查询自己的信息
     * @param token
     * @return
     */
    @GET("/auth/user")
    public Call<HttpBean<UserBean>> getUser(@Query("token") String token);

    /**
     * 查询成员
     * @param pk
     * @param token
     * @return
     */
    @GET("/robots/{robot_pk}/members")
    public Call<HttpBean<List<UserBean>>> getMembers(@Path("robot_pk") String pk,
                                                @Query("token") String token);

    /**
     * 邀请成员
     * @param pk
     * @param token
     * @param mobile
     * @return
     */
    @FormUrlEncoded
    @POST("/robots/{robot_pk}/members")
    public Call<HttpBean> inviteMember(@Path("robot_pk") String pk,
                                              @Field("token") String token,
                                              @Field("mobile") String mobile);

    /**
     * 更改成员权限
     * @param robot_pk
     * @param pk
     * @param token
     * @param role 0，普通成员，1，管理员
     * @return
     */
    @FormUrlEncoded
    @PUT("/robots/{robot_pk}/members/{pk}")
    public Call<HttpBean> optionMember(@Path("robot_pk") String robot_pk,
                        @Path("pk") String pk,@Field("token") String token, @Field("role") String role);

    /**
     * 删除成员
     * @param robot_pk
     * @param pk
     * @param token
     * @return
     */
    @DELETE("/robots/{robot_pk}/members/{pk}")
    public Call<HttpBean> removeMember(@Path("robot_pk") String robot_pk,
                                             @Path("pk") String pk,@Query("token") String token);
}
