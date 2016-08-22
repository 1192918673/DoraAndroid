package coms.geeknewbee.doraemon.entity;

import java.util.List;

/**
 * Created by chensiyuan on 2016/5/4.
 * Desc：
 */
public class SmsNotify {

    /**
     * code : 200
     * data : [{"id":6,"content":"已邀请【ssss】加入家庭成员","date_created":"2016-05-04 15:19:24"},{"id":5,"content":"【First】电量低，请及时充电","date_created":"2016-05-04 15:07:41"},{"id":4,"content":"【First】学习英语得分【88】","date_created":"2016-05-04 15:07:03"},{"id":3,"content":"【First】学习英语得分【0】","date_created":"2016-05-04 15:06:57"},{"id":2,"content":"【First】学习英语得分【100】","date_created":"2016-05-04 15:06:53"},{"id":1,"content":"【First】学习英语得分【92】","date_created":"2016-05-04 15:00:10"}]
     */

    private int code;
    /**
     * id : 6
     * content : 已邀请【ssss】加入家庭成员
     * date_created : 2016-05-04 15:19:24
     */

    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
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
}
