package coms.geeknewbee.doraemon.index.center.biz;

/**
 * Created by chen on 2016/4/13
 */
public interface ISuggestionBiz {
    void sendSuggestion(String token, String content, onSendListener listener);
    interface onSendListener{
        void sendSuccess();
        void sendFailed();
    }

}
