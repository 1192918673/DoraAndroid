package coms.geeknewbee.doraemon.box.learnen.biz;

import java.util.List;

import coms.geeknewbee.doraemon.box.custom_answers.bean.CusAnswersBean;
import coms.geeknewbee.doraemon.box.learnen.bean.LearnEnBean;
import coms.geeknewbee.doraemon.global.HttpBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lenovo on 2016/4/22.
 */
public interface ILearnEnBiz {


    /**
     * 获得学习记录
     * @param robot_pk
     * @param token
     * @param page
     * @return
     */
    @GET("/robots/{robot_pk}/study_records")
    public Call<HttpBean<List<LearnEnBean>>> retrieve(@Path("robot_pk") String robot_pk,
                                                        @Query("token") String token, @Query("page") int page);
}
