package coms.geeknewbee.doraemon.robot.bean;

/**
 * Created by Administrator on 2016/6/1.
 * 机器人
 */

public class MsgBean {

    private int id;

    private String content;

    private String date_created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
