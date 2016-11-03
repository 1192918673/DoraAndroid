package coms.geeknewbee.doraemon.robot.biz;

import java.util.List;

import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.robot.bean.MsgBean;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface IMsgBiz {

    /**
     * 获得消息
     * @param robot_pk
     * @param token
     * @param page
     * @return
     */
    @GET("/robots/{robot_pk}/events")
    public Call<HttpBean<List<MsgBean>>> getMsgs(@Path("robot_pk") String robot_pk,
                                   @Query("token") String token, @Query("page") int page);

    /**
     * 获得消息
     * @param robot_pk
     * @param token
     * @return
     */
    @DELETE("/robots/{robot_pk}/events")
    public Call<HttpBean<List<MsgBean>>> deleteMsgs(@Path("robot_pk") String robot_pk,
                                                   @Query("token") String token);
}
