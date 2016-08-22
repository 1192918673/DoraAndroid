package coms.geeknewbee.doraemon.index.center.view;

import java.io.File;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;

/**
 * Created by chen on 2016/4/12
 */
public interface IEditProfileView extends IBaseView {

    /**
     * 返回center
     */
    void goBack();

    /**
     * 获取昵称
     */
    String getNickName();

    /**
     * 获取性别
     */
    String getGender();

    /**
     * 获取生日
     */
    String getBirth();

    File getAvatar();

    /**
     * 显示进度
     */
    void showLoading();

    /**
     * 隐藏进度
     */
    void hideLoading();

    String getToken();

    void setData(UserBean bean);

    /**
     * 网络访问成功后，更新数据
     */
    void updateUser(UserBean ub);
}
