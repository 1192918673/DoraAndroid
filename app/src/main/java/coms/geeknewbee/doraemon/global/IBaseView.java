package coms.geeknewbee.doraemon.global;

/**
 * Created by chen on 2016/4/6
 * 基础接口，提供一些公用的接口方法
 */
public interface IBaseView {

    /**
     * 显示错误提示信息
     * @param msg
     * @return
     */
    public void showMessage(String msg);

    /**
     * 登录超时的处理
     */
    public void loginTimeout();
}
