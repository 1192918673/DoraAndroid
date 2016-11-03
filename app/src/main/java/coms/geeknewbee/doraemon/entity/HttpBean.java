package coms.geeknewbee.doraemon.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by chen on 2016/3/25
 * 基础网络数据Bean
 */
public class HttpBean<T> {

    public int code;

    public String msg;

    public T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 解析数据
     * @param gson
     * @param typeToken 比如：new TypeToken<HttpBean<Object>>(){}
     * @param <T>
     * @return
     */
    public static <T> HttpBean<T> getBeanFromGson(String gson, TypeToken typeToken){
        ILog.e("Http", gson);
        Gson g = new Gson();
        HttpBean<T> bean = g.fromJson(gson, typeToken.getType());
        return bean;
    }

    @Override
    public String toString() {
        return "code : " + code + " | msg : " + msg;
    }
}
