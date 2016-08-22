package coms.geeknewbee.doraemon.box.alarm_clock.bean;

import java.util.List;

/**
 * Created by chen on 2016/4/15
 */
public class AlarmsBean {
    /**
     * code : 200
     * data : [{"id":2,"content":"去打篮球","alarm_date":null,"alarm_time":"15:40:00","repeat":1,"date_created":"2016-04-15 14:40:33"},
     * {"id":3,"content":"去打篮球","alarm_date":null,"alarm_time":"15:40:00","repeat":1,"date_created":"2016-04-15 14:42:36"},
     * {"id":4,"content":"去打台球","alarm_date":null,"alarm_time":"15:40:00","repeat":1,"date_created":"2016-04-15 14:42:45"},
     * {"id":5,"content":"去提车","alarm_date":"2016-04-21","alarm_time":"16:40:00","repeat":0,"date_created":"2016-04-15 14:43:20"}]
     */

    private int id;
    private String content;
    private String alarm_date;
    private String alarm_time;
    private int repeat;
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

    public String getAlarm_date() {
        return alarm_date;
    }

    public void setAlarm_date(String alarm_date) {
        this.alarm_date = alarm_date;
    }

    public String getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(String alarm_time) {
        this.alarm_time = alarm_time;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "DataEntity{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", alarm_date=" + alarm_date +
                ", alarm_time='" + alarm_time + '\'' +
                ", repeat=" + repeat +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}
