package coms.geeknewbee.doraemon.box.smart_home.view;

import java.util.List;

import coms.geeknewbee.doraemon.box.smart_home.bean.SmartBean;
import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;

/**
 * Created by Administrator on 2016/6/23.
 */
public interface ISmartView extends IBaseView {

    public String getToken();

    public String getRobotPk();

    public int getPage();

    public int getBaseId();

    public String getData();

    public void setSmartBeans(List<SmartBean> smartBeens);

    public void addSuccess();
}
