package coms.geeknewbee.doraemon.box.custom_answers.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/3/31
 */
public interface ICusAnswerAddView extends IBaseView {

    /**
     * 获取输入
     */
    String getQuestion();

    String getAnswer();

    /**
     * 获取token
     */
    String getToken();


    String getRobot_PK();

    /**
     * success
     */
    void showSuccess();
}
