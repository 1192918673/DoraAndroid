package coms.geeknewbee.doraemon.entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 马庆军 on 16/4/28.
 */
public class RobotPhotos {
    public HashMap<String, ArrayList<RobotPhoto>> getData() {
        return data;
    }

    public void setData(HashMap<String, ArrayList<RobotPhoto>> data) {
        this.data = data;
    }

    private HashMap<String, ArrayList<RobotPhoto>> data;
}
