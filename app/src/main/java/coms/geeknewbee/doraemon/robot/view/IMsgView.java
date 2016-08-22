package coms.geeknewbee.doraemon.robot.view;

import java.util.List;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.robot.bean.MsgBean;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface IMsgView extends IBaseView {

    public String getToken();

    public String getRobotPk();

    public int getPage();

    public void setMsgBeans(List<MsgBean> msgBeans);
}
