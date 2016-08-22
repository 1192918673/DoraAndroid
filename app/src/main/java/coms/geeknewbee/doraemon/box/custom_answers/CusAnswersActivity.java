package coms.geeknewbee.doraemon.box.custom_answers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.custom_answers.bean.CusAnswersBean;
import coms.geeknewbee.doraemon.box.custom_answers.presenter.CusAnswersPresenter;
import coms.geeknewbee.doraemon.box.custom_answers.view.ICusAnswersView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by chen on 2016/3/30
 */
public class CusAnswersActivity extends BaseActivity implements ICusAnswersView {

    @Bind(R.id.ib_back)
    ImageButton ib_Back;
    @Bind(R.id.tv_edit)
    TextView tv_Edit;
    @Bind(R.id.edit_template)
    ImageButton edit_template;
    @Bind(R.id.lv_list)
    PullToRefreshListView mLv;
    @Bind(R.id.empty)
    TextView empty;

    private List<CusAnswersBean> mList = new ArrayList<>();
    private List<Integer> selected = new ArrayList<>();//选中的对象

    private boolean edit = false;//编辑or取消界定

    private int page = 1;//页码

    CusAnswersPresenter presenter = new CusAnswersPresenter(this);
    private ListView refreshableView;
    private String strIds;

    String robotPk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_talk);
        ButterKnife.bind(this);
        //设置PTR的模式
        mLv.setMode(PullToRefreshBase.Mode.BOTH);
        initListener();

        robotPk = getIntent().getStringExtra("robotPk");
        showDialog("正在加载数据……");
        presenter.getData();
    }

    private void initListener() {
        refreshableView = mLv.getRefreshableView();
        refreshableView.setAdapter(adapter);

        //条目点击
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        //返回
        ib_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //编辑
        tv_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = !edit;
                if(edit){
                    strIds = "";
                    selected.clear();
                    //1.更新右上角TextView的值
                    tv_Edit.setText("取消");
                    //2.隐藏左上角返回按钮
                    ib_Back.setVisibility(View.GONE);
                    //中间ImageButton替换
                    edit_template.setImageResource(R.mipmap.cancel);
                    //3.更新ListView
                    adapter.notifyDataSetChanged();
                } else {
                    tv_Edit.setText("编辑");
                    ib_Back.setVisibility(View.VISIBLE);
                    edit_template.setImageResource(R.mipmap.add_talk);
                    adapter.notifyDataSetChanged();
                }
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
                    Intent intent = new Intent(CusAnswersActivity.this, AddTalkActivity.class);
                    intent.putExtra("robotPk", robotPk);
                    startActivity(intent);
                }
            }
        });
        mLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        mLv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                page++;
                presenter.getData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(session.contains("ca_refresh")){
            showDialog("正在刷新学说话记录……");
            session.remove("ca_refresh");
            page = 1;
            presenter.getData();
        }
    }

    /**
     * 获取条目点击对应对象的id属性
     *
     * @return
     */
    @Override
    public String getIds() {
        //Selected集合保存的是要删除的对象
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
    public String getRobot_PK() {
        return robotPk;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setData(List<CusAnswersBean> list) {
        hideDialog();
        mLv.onRefreshComplete();
        if(mList != null && page == 1){
            mList.clear();
        }
        if(list != null && list.size() > 0){
            if(page == 1){
                mList = list;
                mLv.setMode(PullToRefreshBase.Mode.BOTH);
            } else
                mList.addAll(list);

        } else {
            if(mList != null && mList.size() > 0){
                mLv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            } else {
                // 没有数据
                mLv.setMode(PullToRefreshBase.Mode.DISABLED);
            }
        }
        if(mList != null && mList.size() > 0){
            mLv.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            // 没有数据
            mLv.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
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
        tv_Edit.setText("编辑");
        ib_Back.setVisibility(View.VISIBLE);
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
            tv_Edit.setText("编辑");
            ib_Back.setVisibility(View.VISIBLE);
            edit_template.setImageResource(R.mipmap.add_talk);
            adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    /**
     * 适配器
     */
    BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            if(mList == null){
                return 0;
            }
            return mList.size();
        }

        @Override
        public CusAnswersBean getItem(int position) {
            return (CusAnswersBean) mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //在getView中获取从map中获取是否选中状态
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_learn_talk, null);
                holder = new ViewHolder();
                //赋值
                holder.tv_Que = (TextView) convertView.findViewById(R.id.tv_que);
                holder.tv_Ans = (TextView) convertView.findViewById(R.id.tv_ans);
                holder.cb_state = (CheckBox) convertView.findViewById(R.id.cb_isSelected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给viewholder里的控件设置内容
            holder.tv_Que.setText(mList.get(position).getQuestion());
            holder.tv_Ans.setText(mList.get(position).getAnswer());
            if(edit){
                holder.cb_state.setVisibility(View.VISIBLE);
                if(selected.contains((Integer) mList.get(position).getId())){
                    holder.cb_state.setChecked(true);
                } else {
                    holder.cb_state.setChecked(false);
                }
            } else {
                holder.cb_state.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    };

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

    static class ViewHolder {
        TextView tv_Que;
        TextView tv_Ans;
        CheckBox cb_state;
    }
}
