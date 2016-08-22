package coms.geeknewbee.doraemon.index.center.presenter;

import android.widget.Toast;

import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.index.center.biz.ISuggestionBiz;
import coms.geeknewbee.doraemon.index.center.biz.impl.SuggestionBiz;
import coms.geeknewbee.doraemon.index.center.view.ISuggestionView;

/**
 * Created by chen on 2016/4/13
 */
public class SuggestionPresenter {

    private ISuggestionView suggestionView;
    private ISuggestionBiz suggestionBiz;

    public SuggestionPresenter(ISuggestionView suggestionView){
        this.suggestionView = suggestionView;
        this.suggestionBiz = new SuggestionBiz();
    }

    public void sendSuggestion(){
        String suggest = suggestionView.getSuggestion();
        if(suggest.trim().length() == 0){
            suggestionView.showMessage("填写内容不能为空！");
            return;
        } else if(suggest.trim().length() > 300) {
            suggestionView.showMessage("填写内容不能超过300字！");
            return;
        }
        suggestionView.showLoading();
        suggestionBiz.sendSuggestion(suggestionView.getToken(), suggestionView.getSuggestion(), new ISuggestionBiz.onSendListener() {
            @Override
            public void sendSuccess() {
                suggestionView.hideLoading();
                Toast.makeText(MyApplication.getContext(),"提交成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void sendFailed() {
                suggestionView.hideLoading();
            }
        });
    }
}
