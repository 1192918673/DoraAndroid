package coms.geeknewbee.doraemon.box.alarm_clock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.alarm_clock.bean.AlarmsBean;
import coms.geeknewbee.doraemon.box.alarm_clock.presenter.AlarmsPresenter;
import coms.geeknewbee.doraemon.box.alarm_clock.view.IAlarmsView;
import coms.geeknewbee.doraemon.box.custom_answers.AddTalkActivity;
import coms.geeknewbee.doraemon.box.custom_answers.CusAnswersActivity;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.utils.DateHandler;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/4/7
 */
public class AlarmsActivity extends BaseActivity implements IAlarmsView {

    private ImageButton ibBack;
    private TextView tvEdit;
    private ImageButton edit_template;
    private PullToRefreshListView lvList;
    TextView empty;

    private List<AlarmsBean> mList = new ArrayList<>();
    private List<Integer> selected = new ArrayList<>();//选中的对象

    private int page = 1;//页码
    private ListView refreshableView;

    private String strIds;//保存要删除的id("131,132,139")

    private boolean edit;//编辑or取消界定

    LayoutInflater mInflater;

    String robotPk;

    private void assignViews() {
        mInflater = getLayoutInflater();

        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        edit_template = (ImageButton) findViewById(R.id.edit_template);
        lvList = (PullToRefreshListView) findViewById(R.id.lv_list);
        empty = (TextView) findViewById(R.id.empty);

        refreshableView = lvList.getRefreshableView();
        refreshableView.setAdapter(adapter);

        robotPk = getIntent().getStringExtra("robotPk");
    }

    private AlarmsPresenter presenter = new AlarmsPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        assignViews();
        //设置PTR的模式
        lvList.setMode(PullToRefreshBase.Mode.BOTH);
        initListener();

        showDialog("正在加载闹钟提醒列表……");
        presenter.getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(session.contains("al_refresh")){
            showDialog("正在刷新闹钟提醒列表……");
            session.remove("al_refresh");
            page = 1;
            presenter.getData();
        }
    }

    //初始化监听器
    private void initListener() {
        //条目点击事件
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!edit){
                    return;
                }

                int num = new Long(id).intValue();

                if(selected.contains((Integer)mList.get(num).getId())){
                    selected.remove((Integer)mList.get(num).getId());
                } else {
                    selected.add((Integer)mList.get(num).getId());
                }

                adapter.notifyDataSetChanged();
            }
        });
        //编辑
        edit_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit){
                    showDialog("正在删除数据……");
                    presenter.delete();
                } else {
                    Intent intent = new Intent(AlarmsActivity.this, AddAlarmsActivity.class);
                    intent.putExtra("robotPk", robotPk);
                    startActivity(intent);
                }
            }
        });
        //编辑
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = !edit;
                if(edit){
                    strIds = "";
                    selected.clear();
                    //1.更新右上角TextView的值
                    tvEdit.setText("取消");
                    //2.隐藏左上角返回按钮
                    ibBack.setVisibility(View.GONE);
                    //中间ImageButton替换
                    edit_template.setImageResource(R.mipmap.cancel);
                    //3.更新ListView
                    adapter.notifyDataSetChanged();
                } else {
                    tvEdit.setText("编辑");
                    ibBack.setVisibility(View.VISIBLE);
                    edit_template.setImageResource(R.mipmap.add_talk);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        //返回
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOff();
            }
        });
        //设置滑动到底部的监听
        lvList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                presenter.getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                presenter.getData();
            }
        });
        lvList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                page++;
                presenter.getData();
            }
        });
    }

    /**
     * 返回到IndexActivity
     */
    public void backOff() {
        startActivity(new Intent(this, IndexActivity.class));
        finish();
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    /**
     * 获取条目点击对应对象的id属性
     *
     * @return
     */
    @Override
    public String getIds() {
        strIds = "";
        for (int i = 0; i < selected.size(); i++) {
            if(i == 0){
                strIds += selected.get(i);
            } else {
                strIds += "," + selected.get(i);
            }
        }
        return strIds;
    }

    @Override
    public String getRobotPK() {
        return robotPk;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void transfer(List<AlarmsBean> list) {
        hideDialog();
        lvList.onRefreshComplete();
        if(mList != null && page == 1){
            mList.clear();
        }
        if(list != null && list.size() > 0){
            if(page == 1){
                mList = list;
                lvList.setMode(PullToRefreshBase.Mode.BOTH);
            } else
                mList.addAll(list);

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

    @Override
    public void deleteSuccess() {
        hideDialog();
        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
        if(mList != null && mList.size() > 0){
            int len = mList.size();
            for(int i = 0; i < len; i++){
                if(selected.contains(mList.get(i).getId())){
                    mList.remove(i);
                    i--;
                    len--;
                }
            }
        }
        edit = false;
        tvEdit.setText("编辑");
        ibBack.setVisibility(View.VISIBLE);
        edit_template.setImageResource(R.mipmap.add_talk);
        adapter.notifyDataSetChanged();
        if(mList.size() == 0){
            showDialog("正在刷新数据……");
            page = 1;
            presenter.getData();
        }
    }

    @Override
    public void onBackPressed() {
        if(edit){
            edit = false;
            tvEdit.setText("编辑");
            ibBack.setVisibility(View.VISIBLE);
            edit_template.setImageResource(R.mipmap.add_talk);
            adapter.notifyDataSetChanged();
        } else {
            backOff();
        }
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }

    /**
     * Created by chensiyuan on 2016/4/18.
     * Desc: Adapter
     */
    BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            if(mList == null)
                return 0;
            return mList.size();
        }

        @Override
        public AlarmsBean getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_little_clock, null);
                holder.data_Time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.desc = (TextView) convertView.findViewById(R.id.tv_desc);
                holder.type = (TextView) convertView.findViewById(R.id.tv_type);
                holder.state = (CheckBox) convertView.findViewById(R.id.cb_isSelected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AlarmsBean dataEntity = mList.get(position);
            String alarm_date = dataEntity.getAlarm_date();
            String alarm_time = dataEntity.getAlarm_time();
            int repeat = dataEntity.getRepeat();
            if(alarm_time != null && alarm_time.length() > 5){
                alarm_time = alarm_time.substring(0, alarm_time.lastIndexOf(":"));
            }
            if (repeat == 1 || null == alarm_date) {
                holder.data_Time.setText(alarm_time);
            } else {
                alarm_date = DateHandler.getDateString(alarm_date);
                if (null == alarm_time) {
                    holder.data_Time.setText(alarm_date);
                } else {
                    holder.data_Time.setText(alarm_date + "  " + alarm_time);
                }
            }
            holder.desc.setText(dataEntity.getContent());
            if (repeat == 0) {
                holder.type.setText("仅一次");
            } else {
                holder.type.setText("每天一次");
            }
            if(edit){
                holder.state.setVisibility(View.VISIBLE);
                if(selected.contains((Integer) mList.get(position).getId())){
                    holder.state.setChecked(true);
                } else {
                    holder.state.setChecked(false);
                }
            } else {
                holder.state.setVisibility(View.GONE);
            }
            return convertView;
        }
    };

    public static class ViewHolder {
        TextView data_Time;
        TextView desc;
        TextView type;
        CheckBox state;
    }
}
