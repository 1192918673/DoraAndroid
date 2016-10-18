package coms.geeknewbee.doraemon.box.time_machine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.time_machine.presenter.IPhotoPresenter;
import coms.geeknewbee.doraemon.box.time_machine.view.IPhotoView;
import coms.geeknewbee.doraemon.entity.RobotPhoto;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.utils.Session;

/**
 * Created by lenovo on 2016/4/21.
 * Desc:时光机
 */
public class TimeMachineActivity extends BaseActivity implements IPhotoView {

    /**
     * -----------------------布局组件----------------------
     **/
    PullToRefreshListView mLv;

    TextView empty;

    ImageButton ib_back;

    ListView refreshableView;

    /**
     * -----------------------数据----------------------
     **/

    private List<String> mList = new ArrayList<>();

    Map<String, List<RobotPhoto>> photos;

    Map<String, PicAdapter> adapterMap = new HashMap<String, PicAdapter>();

    String robotPk;

    int page = 1;

    IPhotoPresenter presenter;

    int lineHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_mac);

        initialize();

        initListener();
    }

    public void initialize() {
        mLv = (PullToRefreshListView) findViewById(R.id.lv_list);
        empty = (TextView) findViewById(R.id.empty);
        ib_back = (ImageButton) findViewById(R.id.ib_back);

        mLv.setTextColor(0xffffffff);
        refreshableView = mLv.getRefreshableView();
        refreshableView.setAdapter(adapter);

        int width = (Integer) session.get(Session.WIDTH);
        float d = (Float) session.get(Session.DENSITY);
        lineHeight = new Float((width - 30 * d) / 3).intValue();

        robotPk = getIntent().getStringExtra("robotPk");

        presenter = new IPhotoPresenter(this);

        showDialog("正在加载数据……");
        presenter.getPhotos();
    }

    @Override
    public void onBackPressed() {
        backOff();
    }

    /**
     * 返回到IndexActivity
     */
    public void backOff() {
        startActivity(new Intent(this, IndexActivity.class));
        finish();
    }

    public void initListener() {
        //返回
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOff();
            }
        });
        mLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                presenter.getPhotos();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                presenter.getPhotos();
            }
        });
        mLv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                page++;
                presenter.getPhotos();
            }
        });
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public String getRobotPk() {
        return robotPk;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public String getIds() {
        return null;
    }

    @Override
    public void setData(Map<String, List<RobotPhoto>> photos) {
        hideDialog();
        mLv.onRefreshComplete();
        if (page == 1) {
            mList.clear();
        }
        if (photos != null && photos.keySet().size() > 0) {
            for (String key : photos.keySet()) {
                if (!mList.contains(key))
                    mList.add(key);
            }
            if (page == 1) {
                this.photos = photos;
                mLv.setMode(PullToRefreshBase.Mode.BOTH);
            } else {
                for (String key : photos.keySet()) {
                    if (!this.photos.containsKey(key))
                        this.photos.put(key, photos.get(key));
                    else
                        this.photos.get(key).addAll(photos.get(key));
                }
            }
        } else {
            if (mList.size() > 0) {
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
        Collections.sort(mList, comparator);
        session.put("tm_photos", this.photos);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.contains("pic_refresh")) {
            session.remove("pic_refresh");
            if (this.photos.keySet().size() == 0) {
                showDialog("正在刷新数据……");
                page = 1;
                presenter.getPhotos();
            } else {
                mList.clear();
                for (String key : photos.keySet()) {
                    mList.add(key);
                }
                Collections.sort(mList, comparator);
                adapter.notifyDataSetChanged();
            }
        }
    }

    Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            return rhs.compareTo(lhs);
        }
    };

    @Override
    public void deleteSuccess() {

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
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
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
                convertView = View.inflate(getApplicationContext(), R.layout.item_pic_p, null);
                holder = new ViewHolder();
                //赋值
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                holder.pic_list = (GridView) convertView.findViewById(R.id.pic_list);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给viewholder里的控件设置内容
            holder.tv_date.setText(mList.get(position));
            if (adapterMap.containsKey(mList.get(position))) {

            } else {
                PicAdapter adapter = new PicAdapter(TimeMachineActivity.this,
                        photos.get(mList.get(position)), robotPk);
                adapterMap.put(mList.get(position), adapter);
            }
            holder.pic_list.setAdapter(adapterMap.get(mList.get(position)));
            holder.pic_list.setOnItemClickListener(adapterMap
                    .get(mList.get(position)).getItemClickListener());
            holder.pic_list.getLayoutParams().height =
                    adapterMap.get(mList.get(position)).getLines() * lineHeight;

            return convertView;
        }
    };

    public static class ViewHolder {
        public TextView tv_date;
        public GridView pic_list;
    }
}
