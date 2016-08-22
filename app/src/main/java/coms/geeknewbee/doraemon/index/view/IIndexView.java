package coms.geeknewbee.doraemon.index.view;

import java.util.List;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface IIndexView extends IBaseView {

    public String getToken();

    public void setData(List<RobotBean> robotBeans);

    public void setData(UserBean user);
}
