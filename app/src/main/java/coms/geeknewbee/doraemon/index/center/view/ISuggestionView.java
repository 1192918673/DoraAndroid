package coms.geeknewbee.doraemon.index.center.view;

import coms.geeknewbee.doraemon.global.IBaseView;

/**
 * Created by chen on 2016/4/13
 */
public interface ISuggestionView extends IBaseView {

    String goBack();

    String getSuggestion();

    void showLoading();

    void hideLoading();

    String getToken();

    void clear();


}
