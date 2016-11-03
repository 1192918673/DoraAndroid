package coms.geeknewbee.doraemon.box.storeroom.biz;

import java.util.List;

import coms.geeknewbee.doraemon.box.storeroom.bean.StoreRoomBean;
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
public interface IStoreRoomBiz {

    /**
     * 获得储物记录
     * @param robot_pk
     * @param token
     * @param page
     * @return
     */
    @GET("/robots/{robot_pk}/goods")
    public Call<HttpBean<List<StoreRoomBean>>> getGoods(@Path("robot_pk") String robot_pk,
                                                        @Query("token") String token, @Query("page") int page);

    /**
     * 添加储物记录
     * @param pk
     * @param token
     * @param name
     * @param place
     * @return
     */
    @FormUrlEncoded
    @POST("/robots/{robot_pk}/goods")
    public Call<HttpBean> addGood(@Path("robot_pk") String pk, @Field("token") String token,
                                    @Field("name") String name, @Field("place") String place);

    /**
     * 删除储物记录
     * @param robot_pk
     * @param token
     * @param pk 要删除的对话ID，多个以逗号隔开
     * @return
     */
    @DELETE("/robots/{robot_pk}/goods")
    public Call<HttpBean> deleteGoods(@Path("robot_pk") String robot_pk,
                                        @Query("token") String token, @Query("pk") String pk);
}
