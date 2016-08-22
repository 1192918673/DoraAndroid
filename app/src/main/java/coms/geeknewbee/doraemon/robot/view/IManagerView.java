package coms.geeknewbee.doraemon.robot.view;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface IManagerView extends IBaseView {

    public String getToken();

    public String getRobotPk();

    public String getName();

    public int getVolume();

    public RobotBean getRobot();

    public void setRobotBean(RobotBean robot);
}
