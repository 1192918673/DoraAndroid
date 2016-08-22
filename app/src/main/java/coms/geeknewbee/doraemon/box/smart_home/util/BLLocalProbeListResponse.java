package coms.geeknewbee.doraemon.box.smart_home.util;

import java.util.List;


/**
 * Created by echo on 16/3/22.
 */
public class BLLocalProbeListResponse extends BLLocalResponse {

    private List<BLLocalDevice> list;

    public void setList(List<BLLocalDevice> list) {
        this.list = list;
    }

    public List<BLLocalDevice> getList() {
        return list;
    }
}
