package coms.geeknewbee.doraemon.box.movie.presenter;

import java.util.List;

import coms.geeknewbee.doraemon.box.movie.bean.MovieBean;
import coms.geeknewbee.doraemon.box.movie.biz.IMovieBiz;
import coms.geeknewbee.doraemon.box.movie.view.IMovieView;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.entity.HttpBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.StringHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by chen on 2016/6/17
 */
public class MoviePresenter {
    private IMovieBiz movieBiz;
    private IMovieView movieView;

    public MoviePresenter(IMovieView movieView){
        this.movieView = movieView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalContants.ROBOTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieBiz = retrofit.create(IMovieBiz.class);
    }

    /**
     * 从服务器获取数据
     */
    public void getData() {
        String token = movieView.getToken();
        String keyword = movieView.getKeyword();
        if(keyword.length() == 0){
            movieView.showMessage("搜索关键词不能为空。");
            return;
        }
        Call<HttpBean<List<MovieBean>>> call = movieBiz.getMovies(token, keyword);

        call.enqueue(new Callback<HttpBean<List<MovieBean>>>() {
            @Override
            public void onResponse(Response<HttpBean<List<MovieBean>>> response) {
                if(response.code() == 200){
                    HttpBean<List<MovieBean>> bean = response.body();
                    if(bean.getCode() == 200){
                        ILog.e("Http", "" + bean.getData().toString());
                        movieView.setData(bean.getData());
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        movieView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        movieView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        movieView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        movieView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                movieView.showMessage("" + t.getMessage());
            }
        });
    }


    /**
     * 发送指定id和token删除对应对话模板
     */
    public void sengMovie() {
        String token = movieView.getToken();
        String robot_pk = movieView.getRobotPk();
        String vid = movieView.getVid();
        Call<HttpBean> call = movieBiz.sendMovie(token, vid, robot_pk);

        call.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Response<HttpBean> response) {
                if(response.code() == 200){
                    HttpBean bean = response.body();
                    if(bean.getCode() == 200){
                        movieView.showMessage("已经发送给您的哆啦A梦，请稍等。");
                    } else if(bean.getCode() == 403 && bean.getMsg() != null
                            && bean.getMsg().contains("Invalid token")){
                        // 登录超时
                        movieView.loginTimeout();
                    } else {
                        String msg = StringHandler
                                .fromUnicode("" + bean.getMsg().replaceAll(" ", ""));
                        ILog.e("Http", msg);
                        movieView.showMessage("" + msg);
                    }
                } else {
                    try{
                        ILog.e("Http", "" + response.errorBody().string());
                        movieView.showMessage("" + response.errorBody().string());
                    } catch (Exception e){
                        ILog.e("Http", "" + e.getMessage());
                        ILog.e(e);
                        movieView.showMessage("" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ILog.e("Http", "" + t.getMessage());
                ILog.e(t);
                movieView.showMessage("" + t.getMessage());
            }
        });
    }
}
