package coms.geeknewbee.doraemon.box.custom_answers.biz;

import java.util.List;

import coms.geeknewbee.doraemon.box.custom_answers.bean.CusAnswersBean;
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
public interface ICABiz {

    /**
     * 获得对话记录
     * @param robot_pk
     * @param token
     * @param page
     * @return
     */
    @GET("/robots/{robot_pk}/custom_answers")
    public Call<HttpBean<List<CusAnswersBean>>> getAnswers(@Path("robot_pk") String robot_pk,
                                     @Query("token") String token, @Query("page") int page);

    /**
     * 添加对话记录
     * @param pk
     * @param token
     * @param question
     * @param answer
     * @return
     */
    @FormUrlEncoded
    @POST("/robots/{robot_pk}/custom_answers")
    public Call<HttpBean> addAnswer(@Path("robot_pk") String pk, @Field("token") String token,
                                    @Field("question") String question, @Field("answer") String answer);

    /**
     * 删除对话记录
     * @param robot_pk
     * @param token
     * @param pk 要删除的对话ID，多个以逗号隔开
     * @return
     */
    @DELETE("/robots/{robot_pk}/custom_answers")
    public Call<HttpBean> deleteAnswers(@Path("robot_pk") String robot_pk,
                                        @Query("token") String token, @Query("pk") String pk);
}
