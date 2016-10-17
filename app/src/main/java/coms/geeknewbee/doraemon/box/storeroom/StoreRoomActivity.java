package coms.geeknewbee.doraemon.box.storeroom;

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

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.storeroom.bean.StoreRoomBean;
import coms.geeknewbee.doraemon.box.storeroom.presenter.StoreRoomPresenter;
import coms.geeknewbee.doraemon.box.storeroom.view.IStoreRoomView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;

/**
 * Created by chen on 2016/4/8
 */
public class StoreRoomActivity extends BaseActivity implements IStoreRoomView {

    private ImageButton ibBack;
    private TextView tvEdit;
    private ImageButton edit_template;
    private TextView empty;
    private PullToRefreshListView mLv;

    private List<StoreRoomBean> mList = new ArrayList<>();
    private List<Integer> selected = new ArrayList<>();//选中的对象

    private boolean edit;//编辑or取消界定

    private int page = 1;//页码

    private String strIds;//保存要删除的id("131,132,139")
    private ListView refreshableView;

    String robotPk;

    StoreRoomPresenter presenter = new StoreRoomPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_room);
        assignViews();
        initListener();
    }

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        edit_template = (ImageButton) findViewById(R.id.edit_template);
        empty = (TextView) findViewById(R.id.empty);
        mLv = (PullToRefreshListView) findViewById(R.id.lv_list);

        robotPk = getIntent().getStringExtra("robotPk");

        //设置PTR的模式
        mLv.setMode(PullToRefreshBase.Mode.BOTH);
        mLv.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新..");
        mLv.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新...");
        refreshableView = mLv.getRefreshableView();
        refreshableView.setAdapter(adapter);

        showDialog("正在加载储物记录……");
        presenter.getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.contains("good_refresh")) {
            showDialog("正在刷新储物记录……");
            session.remove("good_refresh");
            page = 1;
            presenter.getData();
        }
    }

    /**
     * 返回到IndexActivity
     */
    public void backOff() {
        startActivity(new Intent(this, IndexActivity.class));
        finish();
    }

    private void initListener() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!edit) {
                    return;
                }

                int num = new Long(id).intValue();

                if (selected.contains((Integer) mList.get(num).getId())) {
                    selected.remove((Integer) mList.get(num).getId());
                } else {
                    selected.add((Integer) mList.get(num).getId());
                }

                adapter.notifyDataSetChanged();
            }
        });
        //返回
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOff();
            }
        });
        //编辑
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = !edit;
                if (edit) {
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
        //编辑
        edit_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit) {
                    showDialog("正在删除数据……");
                    presenter.delete();
                } else {
                    Intent intent = new Intent(StoreRoomActivity.this, AddGoodsActivity.class);
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

    static class ViewHolder {
        TextView tv_goods;
        TextView tv_place;
        CheckBox cb_state;
    }

    //适配器
    BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            if (mList == null)
                return 0;
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
                convertView = View.inflate(parent.getContext(), R.layout.item_store_room, null);
                holder = new ViewHolder();
                //赋值
                holder.tv_goods = (TextView) convertView.findViewById(R.id.tv_goods);
                holder.tv_place = (TextView) convertView.findViewById(R.id.tv_place);
                holder.cb_state = (CheckBox) convertView.findViewById(R.id.cb_isSelected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给viewholder里的控件设置内容
            StoreRoomBean bean = (StoreRoomBean) mList.get(position);
            holder.tv_goods.setText(bean.getName());
            holder.tv_place.setText(bean.getPlace());
            if (edit) {
                holder.cb_state.setVisibility(View.VISIBLE);
                if (selected.contains((Integer) mList.get(position).getId())) {
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

    //设置关闭动画
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
    public void setData(List<StoreRoomBean> stores) {
        hideDialog();
        mLv.onRefreshComplete();
        if (mList != null && page == 1) {
            mList.clear();
        }
        if (stores != null && stores.size() > 0) {
            if (page == 1) {
                mList = stores;
                mLv.setMode(PullToRefreshBase.Mode.BOTH);
            } else
                mList.addAll(stores);

        } else {
            if (mList != null && mList.size() > 0) {
                mLv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            } else {
                // 没有数据
                mLv.setMode(PullToRefreshBase.Mode.DISABLED);
            }
        }
        if (mList != null && mList.size() > 0) {
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
    public void onBackPressed() {
        if (edit) {
            edit = false;
            tvEdit.setText("编辑");
            ibBack.setVisibility(View.VISIBLE);
            edit_template.setImageResource(R.mipmap.add_talk);
            adapter.notifyDataSetChanged();
        } else {
            backOff();
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
            if (i == 0) {
                strIds += selected.get(i);
            } else {
                strIds += "," + selected.get(i);
            }
        }
        return strIds;
    }

    @Override
    public void deleteSuccess() {
        hideDialog();
        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
        if (mList != null && mList.size() > 0) {
            int len = mList.size();
            for (int i = 0; i < len; i++) {
                if (selected.contains(mList.get(i).getId())) {
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
        if (mList.size() == 0) {
            showDialog("正在刷新数据……");
            page = 1;
            presenter.getData();
        }
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
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }
}
