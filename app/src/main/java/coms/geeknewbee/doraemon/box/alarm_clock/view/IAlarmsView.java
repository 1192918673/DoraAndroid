package coms.geeknewbee.doraemon.box.alarm_clock.view;

import java.util.List;

import coms.geeknewbee.doraemon.box.alarm_clock.bean.AlarmsBean;
import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/4/15
 */
public interface IAlarmsView extends IBaseView {

    /**
     * 获取robot_pk
     */
    String getRobotPK();

    /**
     * 获取page
     */
    int getPage();

    /**
     * 获取条目点击的单个对象的id
     */
    String getIds();

    void transfer(List<AlarmsBean> data);

    String getToken();

    void deleteSuccess();

}
