package coms.geeknewbee.doraemon.global;

import java.util.Map;

/**
 * Created by chen on 2016/3/28
 * 网络访问监听
 */
public interface IHttpListener {
    public void success(Map<String, Object> data);
    public void failed(String errorMsg);
}
