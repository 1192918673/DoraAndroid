package coms.geeknewbee.doraemon.robot.biz;

import java.util.List;

import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.HardInfoBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
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
public interface IRobotBiz {

    /**
     * 查询自己的信息
     * @param token
     * @return
     */
    @GET("/auth/user")
    public Call<HttpBean<UserBean>> getUser(@Query("token") String token);

    /**
     * 绑定机器人
     * @param token
     * @param name 昵称
     * @param volume 音量
     * @param serial_no 机器人序列号
     * @return
     */
    @FormUrlEncoded
    @POST("/robots")
    public Call<HttpBean<RobotBean>> addRobot(@Field("token") String token,
                    @Field("name") String name, @Field("volume") int volume,
                    @Field("serial_no") String serial_no);

    /**
     * 绑定机器人
     * @param token
     * @return
     */
    @GET("/robots")
    public Call<HttpBean<List<RobotBean>>> getRobots(@Query("token") String token);

    /**
     * 修改机器人信息机器人
     * @param token
     * @return
     */
    @FormUrlEncoded
    @PUT("/robots/{pk}")
    public Call<HttpBean<RobotBean>> modifyRobot(@Path("pk") String pk, @Field("token") String token,
                                             @Field("name") String name, @Field("volume") int volume);

    /**
     * 查询机器人硬件信息
     * @param pk
     * @param token
     * @return
     */
    @GET("/robots/{robot_pk}/info")
    public Call<HttpBean<HardInfoBean>> getHardInfo(@Path("robot_pk") String pk,
                                                    @Query("token") String token);

    /**
     * 解绑机器人
     * @param token
     * @return
     */
    @DELETE("/robots/{pk}")
    public Call<HttpBean<RobotBean>> removeRobot(@Path("pk") String pk, @Query("token") String token);
}
