package coms.geeknewbee.doraemon.box.learnen.view;

import java.util.List;

import coms.geeknewbee.doraemon.box.learnen.bean.LearnEnBean;
import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by lenovo on 2016/4/22.
 */
public interface ILearnEnView extends IBaseView {

    String getToken();

    String getRobotID();

    int getPage();

    void transfer(List<LearnEnBean> list);
}
