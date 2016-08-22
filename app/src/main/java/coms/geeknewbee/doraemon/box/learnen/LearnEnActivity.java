package coms.geeknewbee.doraemon.box.learnen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.learnen.bean.LearnEnBean;
import coms.geeknewbee.doraemon.box.learnen.presenter.LearnEnPresenter;
import coms.geeknewbee.doraemon.box.learnen.view.ILearnEnView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;

/**
 * Created by chen on 2016/4/15
 */
public class LearnEnActivity extends BaseActivity implements ILearnEnView {

    private ImageButton ibBack;
    private PullToRefreshListView lvList;
    TextView empty;

    private int page = 1;//页码

    String robotPk;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.en_back);
        lvList = (PullToRefreshListView) findViewById(R.id.en_list);
        empty = (TextView) findViewById(R.id.empty);

        robotPk = getIntent().getStringExtra("robotPk");
    }

    private ListView refreshableView;
    private List<LearnEnBean> mList = new ArrayList<>();

    LearnEnPresenter presenter = new LearnEnPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learnen);
        assignViews();
        presenter.retrieve();
        //设置PTR的模式
        lvList.setMode(PullToRefreshBase.Mode.BOTH);
        initListener();

        refreshableView = lvList.getRefreshableView();
        refreshableView.setAdapter(adapter);
    }

    private void initListener() {
        //返回
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lvList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                presenter.retrieve();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                presenter.retrieve();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public String getRobotID() {
        return robotPk;
    }

    @Override
    public void transfer(List<LearnEnBean> list) {
        hideDialog();
        lvList.onRefreshComplete();
        if(mList != null && page == 1){
            mList.clear();
        }
        if(list != null && list.size() > 0){
            if(page == 1){
                mList = list;
                lvList.setMode(PullToRefreshBase.Mode.BOTH);
            } else {
                mList.addAll(list);
                lvList.setMode(PullToRefreshBase.Mode.BOTH);
            }
        } else {
            if(mList != null && mList.size() > 0){
                lvList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            } else {
                // 没有数据
                lvList.setMode(PullToRefreshBase.Mode.DISABLED);
            }
        }
        if(mList != null && mList.size() > 0){
            lvList.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            // 没有数据
            lvList.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tv_date;
        ImageView iv_star;
        TextView tv_score;
    }

    //适配器
    BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            if(mList == null){
                return 0;
            }
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = View.inflate(parent.getContext(), R.layout.item_learn_en, null);
                holder = new ViewHolder();
                //赋值
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tv_score = (TextView) convertView.findViewById(R.id.tv_score);
                holder.iv_star = (ImageView) convertView.findViewById(R.id.iv_star);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给viewholder里的控件设置内容date_created
            LearnEnBean bean = mList.get(position);
            holder.tv_date.setText(bean.getDate_created());
            holder.tv_score.setText(bean.getScore() + "");
            if (bean.getScore() > 89) {
                //89以上 星星
                holder.iv_star.setVisibility(View.VISIBLE);
                holder.tv_score.setTextColor(Color.parseColor("#FF00A0E8"));
            } else {
                //90一下 没有星星
                holder.iv_star.setVisibility(View.INVISIBLE);
                if (bean.getScore() < 60) {
                    //小于60 红色
                    holder.tv_score.setTextColor(Color.parseColor("#FFFC5950"));
                } else {
                    holder.tv_score.setTextColor(Color.parseColor("#FF00A0E8"));
                }
            }
            return convertView;
        }
    };

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
