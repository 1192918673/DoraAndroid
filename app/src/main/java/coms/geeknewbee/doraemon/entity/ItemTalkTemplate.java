package coms.geeknewbee.doraemon.entity;

/**
 * Created by chen on 2016/3/17
 */
public class ItemTalkTemplate {
    public String que;
    public String ans;
    public boolean state;

    public ItemTalkTemplate() {
    }

    public ItemTalkTemplate(String que, String ans) {
        this.que = que;
        this.ans = ans;

    }

    public ItemTalkTemplate(String que, String ans, boolean state) {
        this.que = que;
        this.ans = ans;
        this.state = state;
    }
}
