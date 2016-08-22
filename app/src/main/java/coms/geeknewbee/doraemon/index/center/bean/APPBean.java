package coms.geeknewbee.doraemon.index.center.bean;

/**
 * Created by chen on 2016/6/22
 * 用户信息
 */
public class APPBean {

    private String apk;
    private int last_version_code;

    public String getApk() {
        return apk;
    }

    public void setApk(String apk) {
        this.apk = apk;
    }

    public int getLast_version_code() {
        return last_version_code;
    }

    public void setLast_version_code(int last_version_code) {
        this.last_version_code = last_version_code;
    }

    @Override
    public String toString() {
        return apk + " / " + last_version_code;
    }
}
