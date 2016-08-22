package coms.geeknewbee.doraemon.index.center.view;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.index.center.bean.APPBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;

/**
 * Created by chen on 2016/4/12
 */
public interface ICenterView extends IBaseView {


    void goBack();

    /**
     * 显示
     */
    void showLoading();

    /**
     * 隐藏进度
     */
    void hideLoading();

    String getToken();

    void setData(UserBean bean);

    void setAPP(APPBean app);
}
