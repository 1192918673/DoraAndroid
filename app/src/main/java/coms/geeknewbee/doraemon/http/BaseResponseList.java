package coms.geeknewbee.doraemon.http;

import java.util.List;

/**
 * Created by dou on 2016/2/9.
 */
public class BaseResponseList<T> {
    private int code;

    private String msg;

    private List<T> data;


    public List<T> getResponseParams() {
        return data;
    }

    public String getError() {
        return msg;
    }

    public int getStatus() {
        return 200;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}
