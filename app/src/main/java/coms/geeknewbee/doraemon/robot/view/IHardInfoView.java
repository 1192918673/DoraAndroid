package coms.geeknewbee.doraemon.robot.view;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.robot.bean.HardInfoBean;

/**
 * Created by Administrator on 2016/6/1.
 */
public interface IHardInfoView extends IBaseView {

    public String getToken();

    public String getRobotPk();

    public void setHardInfo(HardInfoBean hardInfo);
}
