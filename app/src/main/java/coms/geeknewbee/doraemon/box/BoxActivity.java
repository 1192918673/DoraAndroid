package coms.geeknewbee.doraemon.box;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
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
import coms.geeknewbee.doraemon.box.alarm_clock.AlarmsActivity;
import coms.geeknewbee.doraemon.box.custom_answers.CusAnswersActivity;
import coms.geeknewbee.doraemon.box.custom_answers.bean.ItemGvBox;
import coms.geeknewbee.doraemon.box.facetime.FaceTimeActivity;
import coms.geeknewbee.doraemon.box.learnen.LearnEnActivity;
import coms.geeknewbee.doraemon.box.movie.MoviesActivity;
import coms.geeknewbee.doraemon.box.sendvoice.SendVoiceActivity;
import coms.geeknewbee.doraemon.box.smart_home.SmartHomeActivity;
import coms.geeknewbee.doraemon.box.smart_home.SmartPurifierCodeActivity;
import coms.geeknewbee.doraemon.box.smart_home.SmartTVWifiActivity;
import coms.geeknewbee.doraemon.box.storeroom.StoreRoomActivity;
import coms.geeknewbee.doraemon.box.time_machine.TimeMachineActivity;
import coms.geeknewbee.doraemon.index.IndexActivity;

/**
 * Created by chen on 2016/3/16
 * 百宝箱页面
 */
public class BoxActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // 图片封装为一个数组
//    private int[] icon = {R.mipmap.conversation, R.mipmap.learntalk, R.mipmap.english,
//            R.mipmap.clock, R.mipmap.smarthome, R.mipmap.facetime, R.mipmap.storeroom,
//            R.mipmap.ic_movie, R.mipmap.time};
    private int[] icon = {R.mipmap.conversation, R.mipmap.learntalk, R.mipmap.clock, R.mipmap.smarthome, R.mipmap.storeroom, R.mipmap.ic_movie, R.mipmap.time};
    //文字介绍
    private String[] iconName = {"说话", "学对话", "小闹钟", "智能家居", "储物间", "查电影", "时光机"};
    //    private String[] iconName = {"说话", "学对话", "学英语", "小闹钟", "智能家居", "视频通话", "储物间",
//            "查电影", "时光机"};
    private GridView gv;
    private Button bt_Back;
    private List<ItemGvBox> data_list;

    String robotPk;

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

        robotPk = getIntent().getStringExtra("robotPk");

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
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        return true;
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
//        this.overridePendingTransition(R.anim.abc_fade_out, 0);
        //从上到下退出屏幕效果
        //在这里设置第一个参数时，控制的是IndexActivity从上到下，而不是想要的本类BoxActivity从上到下的去消失。
        //而且之前，在没有去掉BoxActivity背景透明的时候，该动画无效。
    }

    /**
     * 百宝箱入口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:     //说话
                Intent intentVoice = new Intent(BoxActivity.this, SendVoiceActivity.class);
                intentVoice.putExtra("robotPk", robotPk);
                startActivity(intentVoice);
                finish();
                break;
            case 1:     //学对话
                Intent intentCa = new Intent(BoxActivity.this, CusAnswersActivity.class);
                intentCa.putExtra("robotPk", robotPk);
                startActivity(intentCa);
                finish();
                break;
            case 2:     //闹钟
                Intent intentAl = new Intent(BoxActivity.this, AlarmsActivity.class);
                intentAl.putExtra("robotPk", robotPk);
                startActivity(intentAl);
                finish();
                break;
            case 3:     //智能家居
                Intent intentSmart = new Intent(BoxActivity.this, SmartHomeActivity.class);
                intentSmart.putExtra("robotPk", robotPk);
                startActivity(intentSmart);
                finish();
                break;
            case 4:     //储物间
                Intent intentStore = new Intent(BoxActivity.this, StoreRoomActivity.class);
                intentStore.putExtra("robotPk", robotPk);
                startActivity(intentStore);
                finish();
                break;
            case 5:     //查电影
                Intent intentMovie = new Intent(BoxActivity.this, MoviesActivity.class);
                intentMovie.putExtra("robotPk", robotPk);
                startActivity(intentMovie);
                finish();
                break;
            case 6:   //时光机
                //测试TagFlowLayout
                Intent intentTest = new Intent(BoxActivity.this, TimeMachineActivity.class);
                intentTest.putExtra("robotPk", robotPk);
                startActivity(intentTest);
                finish();
                break;
//            case 2:   //学英语
//                Intent intentEn = new Intent(BoxActivity.this, LearnEnActivity.class);
//                intentEn.putExtra("robotPk", robotPk);
//                startActivity(intentEn);
//                finish();
//                break;
//            case 3:   //闹钟
//                Intent intentAl = new Intent(BoxActivity.this, AlarmsActivity.class);
//                intentAl.putExtra("robotPk", robotPk);
//                startActivity(intentAl);
//                finish();
//                break;
//            case 4:   //智能家居
//                Intent intentSmart = new Intent(BoxActivity.this, SmartHomeActivity.class);
//                intentSmart.putExtra("robotPk", robotPk);
//                startActivity(intentSmart);
//                finish();
//                break;
//            case 5:   //视频通话
//                Intent intentLogin = new Intent(BoxActivity.this, FaceTimeActivity.class);
//                intentLogin.putExtra("robotPk", robotPk);
//                startActivity(intentLogin);
//                finish();
//                break;
//            case 6:   //储物间
//                Intent intentStore = new Intent(BoxActivity.this, StoreRoomActivity.class);
//                intentStore.putExtra("robotPk", robotPk);
//                startActivity(intentStore);
//                finish();
//                break;
//            case 7:   //查电影
//                Intent intentMovie = new Intent(BoxActivity.this, MoviesActivity.class);
//                intentMovie.putExtra("robotPk", robotPk);
//                startActivity(intentMovie);
//                finish();
//                break;
//            case 8:   //时光机
//                //测试TagFlowLayout
//                Intent intentTest = new Intent(BoxActivity.this, TimeMachineActivity.class);
//                intentTest.putExtra("robotPk", robotPk);
//                startActivity(intentTest);
//                finish();
//                break;
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
