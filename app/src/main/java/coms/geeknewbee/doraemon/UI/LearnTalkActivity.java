package coms.geeknewbee.doraemon.UI;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.db.DbTalkTemplateDao;
import coms.geeknewbee.doraemon.entity.ItemTalkTemplate;

/**
 * Created by chen on 2016/3/16
 * 学说话页面
 */
public class LearnTalkActivity extends Activity implements View.OnClickListener ,AdapterView.OnItemClickListener{

    private ImageButton ib_Back;
    private TextView tv_Edit;
    private ImageButton ib_Add_Template;
    private DbTalkTemplateDao mTalkTemplateDao;
    private ImageButton ib_Del_Template;

    private ListView mLv;
    private List<ItemTalkTemplate> mList;
    private MyAdapter adapter;

    private int layout = R.layout.item_learn_talk;
    private int layout_edit = R.layout.item_edit_talk;

    private int times;

    //定义成员变量用于记录ListView所有item的选中状态
    private Map<Integer,Boolean> checkStatusMap = new HashMap<Integer,Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_talk);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ib_Back = (ImageButton) findViewById(R.id.ib_back);
        tv_Edit = (TextView) findViewById(R.id.tv_edit);
        ib_Add_Template = (ImageButton) findViewById(R.id.edit_template);
        ib_Del_Template = (ImageButton) findViewById(R.id.edit_template);
        mLv = (ListView) findViewById(R.id.lv_list);
        mLv.setOnItemClickListener(this);
        ib_Back.setOnClickListener(this);
        tv_Edit.setOnClickListener(this);
        ib_Add_Template.setOnClickListener(this);
        initData();
    }

    /**
     * 通过数据库查询所有对话模板数据
     * 操作数据库 耗时操作
     */
    public void initData() {
        new Thread() {
            @Override
            public void run() {
                //获取数据，填充数据集合
                mTalkTemplateDao = new DbTalkTemplateDao(getApplication());
                ArrayList list = mTalkTemplateDao.queryAll();
                mList = new ArrayList<ItemTalkTemplate>();
                mList.addAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyAdapter(layout);
                        mLv.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    /**
     * 点击事件的处理
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                //返回首页
                startActivity(new Intent(LearnTalkActivity.this, IndexActivity.class));
                finish();
                break;
            case R.id.tv_edit:
                //进入编辑页面
                if(times%2==0)
                    toEditPage();
                else
                    backPage();
                break;
            case R.id.edit_template:
                //进去学说话 增加页面
                startActivity(new Intent(LearnTalkActivity.this, AddTalkActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    //返回上一页
    private void backPage() {
        times++;
        //1.更新右上角TextView的值
        tv_Edit.setText("编辑");
        //2.隐藏左上角返回按钮
        ib_Back.setVisibility(View.VISIBLE);
        //中间ImageButton替换
        ib_Del_Template.setVisibility(View.GONE);
        ib_Add_Template.setVisibility(View.VISIBLE);
        //3.更新ListView
        adapter = new MyAdapter(layout);
        mLv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //进去学说话编译页面
    private void toEditPage() {
        times++;
        //1.更新右上角TextView的值
        tv_Edit.setText("取消");
        //2.隐藏左上角返回按钮
        ib_Back.setVisibility(View.INVISIBLE);
        //中间ImageButton替换
        ib_Add_Template.setVisibility(View.GONE);
        ib_Del_Template.setVisibility(View.VISIBLE);
        //3.更新ListView
        adapter = new MyAdapter(layout_edit);
        mLv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //取得ViewHolder对象，省去了通过层层的findViewById去实例化需要的cb实例的步骤
        ViewHolder holder = (ViewHolder) view.getTag();
        //把checkbox的选中状态取反
        holder.cb_state.toggle();
        boolean isChecked = holder.cb_state.isChecked();
        //save CheckBox Status
        checkStatusMap.put(position,isChecked);
        //在ListView的onItemClick方法或者CheckBox的onClick方法中记录CheckBox选中状态。
    }

    static class ViewHolder {
        TextView tv_Que;
        TextView tv_Ans;
        CheckBox cb_state;
    }

    /**
     * 适配器
     */
    public class MyAdapter extends BaseAdapter {

        int layout=0;
        public MyAdapter(int layout){
            this.layout= layout;
            //在Adapter初始化时向checkStatusMap中put List数据个数的false.(表示一开始都未选中)
            int position =0;
            for(int i=0; i< mList.size();i++){
                checkStatusMap.put(position++,false);
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public ItemTalkTemplate getItem(int position) {
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
                convertView = View.inflate(getApplicationContext(), layout, null);
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
            ItemTalkTemplate bean = mList.get(position);
            holder.tv_Que.setText(bean.que);
            holder.tv_Ans.setText(bean.ans);
            //从map中获取CheckBox是否选中状态
            holder.cb_state.setChecked(checkStatusMap.get(position));
            return convertView;
        }
    }
}
