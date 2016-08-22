package coms.geeknewbee.doraemon.box.sendvoice.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/3/30
 */
public interface ISendVoiceView extends IBaseView {
    /**
     * 回退
     */
    void backOff();

    /**
     * 获取文字输入
     */
    String getText();

    /**
     * 获取token
     */
    String getToken();

    String getRobotPk();

    /**
     * clear
     */
    void clear();
    /**
     * 成功
     */
    void showSuccess();

    /**
     * Failed
     */
    void showFailed(String error);
}
