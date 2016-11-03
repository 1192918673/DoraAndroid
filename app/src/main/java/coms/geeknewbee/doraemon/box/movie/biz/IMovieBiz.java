package coms.geeknewbee.doraemon.box.movie.biz;

import java.util.List;

import coms.geeknewbee.doraemon.box.movie.bean.MovieBean;
import coms.geeknewbee.doraemon.entity.HttpBean;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/17.
 */
public interface IMovieBiz {

    /**
     * 获得电影列表
     * @param token
     * @param keyword
     * @return
     */
    @GET("/media/videos")
    public Call<HttpBean<List<MovieBean>>> getMovies(@Query("token") String token,
                                                    @Query("keyword") String keyword);

    /**
     * 推送电影播放
     * @param token
     * @param vid
     * @param robot_pk
     * @return
     */
    @FormUrlEncoded
    @POST("/robots/{robot_pk}/video")
    public Call<HttpBean> sendMovie(@Field("token") String token, @Field("vid") String vid,
                                  @Path("robot_pk") String robot_pk);
}
