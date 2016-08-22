package coms.geeknewbee.doraemon.box.time_machine.biz;

import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.entity.RobotPhoto;
import coms.geeknewbee.doraemon.global.HttpBean;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/17.
 */
public interface IPhotoBiz {

    /**
     * 查询相册信息
     * @param pk
     * @param token
     * @param page
     * @return
     */
    @GET("/robots/{robot_pk}/photos")
    public Call<HttpBean<Map<String, List<RobotPhoto>>>> getPhotos(@Path("robot_pk") String pk,
                                         @Query("token") String token, @Query("page") int page);

    /**
     * 删除相片
     * @param robot_pk
     * @param token
     * @param pk
     * @return
     */
    @DELETE("/robots/{robot_pk}/photos")
    public Call<HttpBean> removePhotos(@Path("robot_pk") String robot_pk,
                                       @Query("token") String token, @Query("pk") String pk);
}
