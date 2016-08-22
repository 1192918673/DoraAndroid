package coms.geeknewbee.doraemon.box.alarm_clock.bean;

/**
 * Created by chen on 2016/4/8
 */
public class ItemClockBean {
    private String time;
    private String desc;
    private String type;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItemClockBean() {
    }

    public ItemClockBean(String time, String desc, String type) {
        this.time = time;
        this.desc = desc;
        this.type = type;
    }
}
