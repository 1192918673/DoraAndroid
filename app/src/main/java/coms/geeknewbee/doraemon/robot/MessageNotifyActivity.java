package coms.geeknewbee.doraemon.robot;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.bean.MsgBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotMsgPresenter;
import coms.geeknewbee.doraemon.robot.view.IMsgView;

/**
 * Created by chensiyuan on 2016/5/4.
 * Desc：设备管理下的消息通知Activity
 */
public class MessageNotifyActivity extends BaseActivity implements IMsgView,
        View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ImageButton ibBack;

    private TextView clear;

    private PullToRefreshListView lv;

    private ListView refreshableView;

    View empty;

    SmsAdapter adapter;

    int robotId;

    int page = 1;

    List<MsgBean> msgBeanList;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        clear = (TextView) findViewById(R.id.clear);
        lv = (PullToRefreshListView) findViewById(R.id.lv);
        empty = findViewById(R.id.empty);

        robotId = getIntent().getIntExtra("robotId", 0);
    }

    private boolean flag = false;//仅一次标记

    private IRobotMsgPresenter presenter = new IRobotMsgPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsnotify);
        assignViews();
        showDialog("正在加载信息……");
        presenter.loadMsgs();
        lv.setMode(PullToRefreshBase.Mode.BOTH);
        initListener();
    }

    private void initListener() {
        ibBack.setOnClickListener(this);
        clear.setOnClickListener(this);
        lv.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;

            case R.id.clear:
                if(msgBeanList != null && msgBeanList.size() > 0){
                    showDialog("正在删除信息……");
                    page = 1;
                    presenter.deleteMsgs();
                } else {
                    showMsg("系统提示", "您没有消息可供清空！");
                }
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        presenter.loadMsgs();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        presenter.loadMsgs();
    }

    //首次加载page=1的数据
    private void initData() {
        //传参生成适配器
        adapter = new SmsAdapter(msgBeanList, MessageNotifyActivity.this);
        refreshableView = lv.getRefreshableView();
        refreshableView.setAdapter(adapter);
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public String getRobotPk() {
        return "" + robotId;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setMsgBeans(List<MsgBean> msgBeans) {
        hideDialog();
        lv.onRefreshComplete();
        if(msgBeanList != null && page == 1){
            msgBeanList.clear();
        }
        if(msgBeans != null && msgBeans.size() > 0){
            if(page == 1){
                msgBeanList = msgBeans;
                lv.setMode(PullToRefreshBase.Mode.BOTH);
            } else
                msgBeanList.addAll(msgBeans);

        } else {
            if(msgBeanList != null && msgBeanList.size() > 0){
                lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            } else {
                // 没有数据
                lv.setMode(PullToRefreshBase.Mode.DISABLED);
            }
        }
        if(msgBeanList != null && msgBeanList.size() > 0){
            lv.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            // 没有数据
            lv.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
        if (!flag) {
            //执行一次
            initData();
            flag = true;
        } else {
            adapter.setmListData(msgBeanList);
        }
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }
}
