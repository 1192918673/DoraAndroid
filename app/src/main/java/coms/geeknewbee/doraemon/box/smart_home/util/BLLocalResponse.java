package coms.geeknewbee.doraemon.box.smart_home.util;

/**
 * Created by echo on 16/3/22.
 */
public class BLLocalResponse {

    /**
     * code : 0
     * msg : network init  success
     */

    private int code;
    private String msg;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
