package coms.geeknewbee.doraemon.robot.view;

import java.util.List;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface IMemberView extends IBaseView {

    public String getToken();

    public String getRobotPk();

    public String getMobile();

    public String getUserPk(int position);

    public String getRole(int position);

    public void setUser(UserBean ub);

    public void setUserBeans(List<UserBean> userBeans);

    public void optionSuccess(int type, String pk);
}
