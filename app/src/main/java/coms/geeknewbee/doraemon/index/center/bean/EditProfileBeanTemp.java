package coms.geeknewbee.doraemon.index.center.bean;

import java.io.File;

/**
 * Created by chen on 2016/4/12
 */
public class EditProfileBeanTemp {

    /**
     * code : 200
     * data : {"id":4,"mobile":"18601234360","nickname":"seven12","avatar":"http://doraemon.microfastup.com/media/avatar/2016/04/12/test.png","gender":"未知","birthday":null}
     */

    private int code;
    /**
     * id : 4
     * mobile : 18601234360
     * nickname : seven12
     * avatar : http://doraemon.microfastup.com/media/avatar/2016/04/12/test.png
     * gender : 未知
     * birthday : null
     */

    private DataEntity data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "EditProfileBeanTemp{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private int id;
        private String mobile;
        private String nickname;
        private File avatar;
        private String gender;
        private String birthday;

        public void setId(int id) {
            this.id = id;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setAvatar(File avatar) {
            this.avatar = avatar;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public int getId() {
            return id;
        }

        public String getMobile() {
            return mobile;
        }

        public String getNickname() {
            return nickname;
        }

        public File getAvatar() {
            return avatar;
        }

        public String getGender() {
            return gender;
        }

        public String getBirthday() {
            return birthday;
        }

        @Override
        public String toString() {
            return "DataEntity{" +
                    "id=" + id +
                    ", mobile='" + mobile + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", avatar=" + avatar +
                    ", gender='" + gender + '\'' +
                    ", birthday='" + birthday + '\'' +
                    '}';
        }
    }
}
