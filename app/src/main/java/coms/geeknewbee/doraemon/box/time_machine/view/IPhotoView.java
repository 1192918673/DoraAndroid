package coms.geeknewbee.doraemon.box.time_machine.view;

import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.entity.RobotPhoto;
import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by Administrator on 2016/6/17.
 */

public interface IPhotoView extends IBaseView {

    public String getToken();

    public String getRobotPk();

    public int getPage();

    public String getIds();

    public void setData(Map<String, List<RobotPhoto>> photos);

    public void deleteSuccess();
}
