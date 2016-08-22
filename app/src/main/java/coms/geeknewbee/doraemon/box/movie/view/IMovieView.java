package coms.geeknewbee.doraemon.box.movie.view;

import java.util.List;

import coms.geeknewbee.doraemon.box.movie.bean.MovieBean;
import coms.geeknewbee.doraemon.box.storeroom.bean.StoreRoomBean;
import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/6/17
 */
public interface IMovieView extends IBaseView {

    /**
     * 获取robot_PK
     */
    String getRobotPk();

    /**
     * 获取token
     */
    String getToken();

    /**
     * 获取keyword
     */
    String getKeyword();

    /**
     * 获取vid
     */
    String getVid();

    /**
     * 设置数据
     */
    void setData(List<MovieBean> movies);
}