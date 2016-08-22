package coms.geeknewbee.doraemon.robot.view;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface IBindView extends IBaseView {

    public String getToken();

    public String getSerialNo();

    public void bindSuccess(RobotBean robotBean);
}
