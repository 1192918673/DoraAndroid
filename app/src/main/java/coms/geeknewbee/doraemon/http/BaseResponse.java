package coms.geeknewbee.doraemon.http;

/*
  http response 的格式类，一般的response有统一的格式
 */
public class BaseResponse<T> {
    private int code;

    private String msg;

    private T data;


    public T getResponseParams() {
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

    public int getCode() {
        return code;
    }
}