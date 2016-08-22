package coms.geeknewbee.doraemon.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.entity.ItemGvBox;

/**
 * Created by chen on 2016/3/16
 * 百宝箱页面
 */
public class BoxActivity extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    // 图片封装为一个数组
    private int[] icon = {R.mipmap.conversation, R.mipmap.learntalk, R.mipmap.english,
            R.mipmap.clock, R.mipmap.smarthome, R.mipmap.facetime, R.mipmap.storeroom,
            R.mipmap.map, R.mipmap.time};
    //文字介绍
    private String[] iconName = {"对话", "学说话", "学英语", "小闹钟", "智能家居",
            "视频通话", "储物间", "地图", "时光机"};
    private GridView gv;
    private Button bt_Back;
    private List<ItemGvBox> data_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        initView();
    }

    private void initView() {
        gv = (GridView) findViewById(R.id.gv);
        gv.setOnItemClickListener(this);
        bt_Back = (Button) findViewById(R.id.bt_back);
        bt_Back.setOnClickListener(this);
        initData();
    }

    /**
     * 为每个item的entity赋值(图片和title)
     */
    private void initData() {
        data_list = new ArrayList<>();
        for (int i = 0; i < icon.length; i++) {
            ItemGvBox igv = new ItemGvBox(icon[i], iconName[i]);
            data_list.add(igv);
        }
        gv.setAdapter(new MyAdapter());
    }

    /**
     * 百宝箱返回按钮
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                //返回首页
                startActivity(new Intent(BoxActivity.this, IndexActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    /**
     * 百宝箱入口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(BoxActivity.this, ConversationActivity.class));
                finish();
                break;
            case 1:
                startActivity(new Intent(BoxActivity.this, LearnTalkActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data_list.size();
        }

        @Override
        public ItemGvBox getItem(int position) {
            return data_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.item_gv, null);
            ImageView img = (ImageView) view.findViewById(R.id.iv_gv_img);
            TextView tv = (TextView) view.findViewById(R.id.tv_gv_title);
            ItemGvBox item = data_list.get(position);
            img.setImageResource(item.iconId);
            tv.setText(item.title);
            return view;
        }
    }
}
