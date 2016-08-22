package coms.geeknewbee.doraemon.http;


import java.util.ArrayList;
import java.util.HashMap;

import coms.geeknewbee.doraemon.entity.RobotPhoto;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by echo on 16/3/17.
 */
public interface APIServices {


    /**
     * 获取某一页图片数据
     *
     * @param token Token
     * @return
     */
    @GET("/robots/{robot_pk}/photos")
    Observable<BaseResponse<HashMap<String, ArrayList<RobotPhoto>>>> robot_photos(@Path("robot_pk") String robot_pk, @Query("token") String token, @Query("page") int page);


    /**
     * 删除单个图片
     *
     * @param token Token
     * @return
     */
    @DELETE("/robots/{robot_pk}/photos")
    Observable<BaseResponse> delete_robot_photos(@Path("robot_pk") String robot_pk, @Query("token") String token, @Query("pk") int pk);


}
