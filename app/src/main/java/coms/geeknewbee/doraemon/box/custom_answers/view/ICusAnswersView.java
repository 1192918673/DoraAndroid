package coms.geeknewbee.doraemon.box.custom_answers.view;

import java.util.List;

import coms.geeknewbee.doraemon.box.custom_answers.bean.CusAnswersBean;
import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/3/30
 */
public interface ICusAnswersView extends IBaseView {

    /**
     * 获取robot_PK
     */
    String getRobot_PK();

    /**
     * 获取page
     */
    int getPage();

    /**
     * 获取条目点击的单个对象的id
     */
    String getIds();

    /**
     * 获取token
     */
    String getToken();

    /**
     * 设置数据
     */
    void setData(List<CusAnswersBean> cas);

    void deleteSuccess();
}
