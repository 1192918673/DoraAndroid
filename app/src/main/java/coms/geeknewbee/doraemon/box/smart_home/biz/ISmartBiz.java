package coms.geeknewbee.doraemon.box.smart_home.biz;

import java.util.List;

import coms.geeknewbee.doraemon.box.smart_home.bean.SmartBean;
import coms.geeknewbee.doraemon.global.HttpBean;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/23.
 */
public interface ISmartBiz {

    /**
     * 查询指令类型数据
     * @param token
     * @param page
     * @return
     */
    @GET("/robots/base_library")
    public Call<HttpBean<List<SmartBean>>> getSmarts(@Query("token") String token,
                                                     @Query("page")  int page);

    /**
     * 提交指令
     * @param pk
     * @param token
     * @param base_id 提交净化器数据时，可以为空
     * @param data 提交遥控器指令时，格式是“MAC地址,指令”，提交净化器数据时，提交扫描到的二维码数据即可
     * @return
     */
    @FormUrlEncoded
    @POST("/robots/{robot_pk}/appliances")
    public Call<HttpBean> addInstructions(@Path("robot_pk") String pk, @Field("token") String token,
                                       @Field("base_id") String base_id, @Field("data") String data);

    /**
     * 获得已经学习的指令
     * @param pk
     * @param token
     * @return
     */
    @GET("/robots/{robot_pk}/appliances")
    public Call<HttpBean<List<SmartBean>>> getInstructions(@Path("robot_pk") String pk,
                                                           @Query("token") String token);
}
