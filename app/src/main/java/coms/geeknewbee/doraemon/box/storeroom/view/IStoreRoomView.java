package coms.geeknewbee.doraemon.box.storeroom.view;

import java.util.List;

import coms.geeknewbee.doraemon.box.storeroom.bean.StoreRoomBean;
import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/4/11
 */
public interface IStoreRoomView extends IBaseView {

    /**
     * 获取robot_PK
     */
    String getRobot_PK();

    /**
     * 获取page
     */
    int getPage();

    /**
     * 获取条目点击的单个对象的id
     */
    String getIds();

    /**
     * 获取token
     */
    String getToken();

    /**
     * 设置数据
     */
    void setData(List<StoreRoomBean> stores);

    void deleteSuccess();
}