package coms.geeknewbee.doraemon.box.alarm_clock.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chensiyuan on 2016/4/19.
 * Desc:
 */
public interface IAlarmsAddView extends IBaseView {

    String getContent();

    String getAlarm_Date();

    String getAlarm_Time();

    int getRepeat();

    String getRobot_PK();

    String getToken();

    void addSuccess();
}
