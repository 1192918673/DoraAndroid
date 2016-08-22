package coms.geeknewbee.doraemon.box.facetime.view;

import java.io.File;

import coms.geeknewbee.doraemon.global.IBaseView;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface IUserView extends IBaseView {

    public String getToken();

    public File getPhoto();

    public String getRobotPk();

    public void setUser(UserBean ub);
}
