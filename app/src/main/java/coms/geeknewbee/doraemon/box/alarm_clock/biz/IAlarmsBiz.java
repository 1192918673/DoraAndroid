package coms.geeknewbee.doraemon.box.alarm_clock.biz;

import java.util.List;

import coms.geeknewbee.doraemon.box.alarm_clock.bean.AlarmsBean;
import coms.geeknewbee.doraemon.entity.HttpBean;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/13.
 */
public interface IAlarmsBiz {

    /**
     * 获得闹钟列表
     * @param robot_pk
     * @param token
     * @param page
     * @return
     */
    @GET("/robots/{robot_pk}/alarms")
    public Call<HttpBean<List<AlarmsBean>>> getAlarms(@Path("robot_pk") String robot_pk,
                                                       @Query("token") String token, @Query("page") int page);

    /**
     * 添加闹钟提醒
     * @param pk
     * @param token
     * @param content
     * @param alarm_date 日期
     * @param alarm_time 时间
     * @param repeat
     * @return
     */
    @FormUrlEncoded
    @POST("/robots/{robot_pk}/alarms")
    public Call<HttpBean> addAlarm(@Path("robot_pk") String pk, @Field("token") String token,
                                    @Field("content") String content, @Field("alarm_date") String alarm_date,
                                    @Field("alarm_time") String alarm_time, @Field("repeat") int repeat);

    /**
     * 删除闹钟记录
     * @param robot_pk
     * @param token
     * @param pk 要删除的对话ID，多个以逗号隔开
     * @return
     */
    @DELETE("/robots/{robot_pk}/alarms")
    public Call<HttpBean> deleteAlarms(@Path("robot_pk") String robot_pk,
                                        @Query("token") String token, @Query("pk") String pk);
}
