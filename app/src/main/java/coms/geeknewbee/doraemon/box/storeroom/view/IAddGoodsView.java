package coms.geeknewbee.doraemon.box.storeroom.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by lenovo on 2016/4/21.
 */
public interface IAddGoodsView extends IBaseView {
    /**
     * 获取输入
     */
    String getName();

    String getPlace();

    /**
     * 获取token
     */
    String getToken();


    String getRobot_PK();

    /**
     * success
     */
    void showSuccess();
}
