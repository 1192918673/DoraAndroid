package coms.geeknewbee.doraemon.box.sendvoice.biz;

import coms.geeknewbee.doraemon.entity.HttpBean;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by chen on 2016/3/30
 */
public interface ISendVoiceBiz {

    /**
     * 发送聊天语音
     * @param pk
     * @param token
     * @param text
     * @return
     */
    @FormUrlEncoded
    @POST("/robots/{robot_pk}/voice")
    public Call<HttpBean> sendText(@Path("robot_pk") String pk, @Field("token") String token,
                                    @Field("text") String text);

}

