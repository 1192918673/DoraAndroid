package coms.geeknewbee.doraemon.index.center.bean;

/**
 * Created by chen on 2016/4/12
 * 用户信息
 */
public class UserBean {

    private int id;
    private String mobile;
    private String nickname;
    private String avatar;
    private String gender;
    private String birthday;
    private String role;
    private HxUser hx_user;

    public void setId(int id) {
        this.id = id;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
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

    public String getAvatar() {
        return avatar;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setHx_user(HxUser hx_user) {
        this.hx_user = hx_user;
    }

    public HxUser getHx_user() {
        return hx_user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static class HxUser {

        private String username;

        private String password;

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }

    @Override
    public String toString() {
        return id + " / " + mobile + " / " + nickname + " / "
                + avatar + " / " + gender + " / " + birthday;
    }
}
