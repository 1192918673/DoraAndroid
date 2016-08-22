package coms.geeknewbee.doraemon.box.movie;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.movie.bean.MovieBean;
import coms.geeknewbee.doraemon.box.movie.presenter.MoviePresenter;
import coms.geeknewbee.doraemon.box.movie.view.IMovieView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chensiyuan on 2016/6/16.
 */
public class MoviesActivity extends BaseActivity implements IMovieView {

    /**----------------------布局组件---------------------**/

    TextView info;
    TextView tvStop;
    ImageView clear;
    EditText et_movie_name;

    ImageView getPic;
    ImageButton ib_back;
    GridView movie_list;

    /**----------------------数据---------------------**/

    MoviePresenter presenter;

    String robotPk;

    String vid;

    List<MovieBean> movies;

    int picWidth;
    int picHeight;

    UserBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // TODO 第一步，初始化数据
        initialize();

        // TODO 第二步，加载数据
        robotPk = getIntent().getStringExtra("robotPk");
        presenter = new MoviePresenter(this);

        if(session.contains(Session.USER) && !session.contains("EMClient.login")){
            user = (UserBean) session.get(Session.USER);
            login();
        }
    }

    private void initialize() {
        // TODO 布局数据
        info = (TextView)findViewById(R.id.info);
        clear = (ImageView) findViewById(R.id.clear);
        et_movie_name = (EditText) findViewById(R.id.et_movie_name);
        movie_list = (GridView)findViewById(R.id.movie_list);

        getPic = (ImageView) findViewById(R.id.get_pic);
        ib_back = (ImageButton)findViewById(R.id.ib_back);
        skm = new SoftKeyboardManager(et_movie_name);
        tvStop = (TextView)findViewById(R.id.tvStop);

        movie_list.setAdapter(adapter);
        movie_list.setOnItemClickListener(itemClickListener);

        ib_back.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
        tvStop.setOnClickListener(clickListener);
        et_movie_name.setOnEditorActionListener(actionListener);
        et_movie_name.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_back:
                    finish();
                    break;
                case R.id.clear:
                    et_movie_name.setText("");
                    break;
                case R.id.tvStop:
                    vid = "stop";
                    sendMovie();
                    break;
            }
        }
    };

    TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId,
                                      KeyEvent event) {
            switch(actionId){
                case  EditorInfo.IME_ACTION_SEARCH: // IME_ACTION_SEARCH 事件时提交搜索
                    showDialog("正在搜索，请稍等……");
                    info.setVisibility(View.VISIBLE);
                    info.setText("哆啦A梦正在搜寻，请稍候");
                    presenter.getData();
                    skm.hide();
                    break;
            }
            return false;
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            vid = movies.get(position).getId();
            // presenter.sengMovie();
            sendMovie();
        }
    };

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public String getRobotPk() {
        return robotPk;
    }

    @Override
    public String getVid() {
        return vid;
    }

    @Override
    public String getKeyword() {
        return et_movie_name.getText().toString().trim();
    }

    @Override
    public void setData(List<MovieBean> movies) {
        hideDialog();
        this.movies = movies;
        if(movies == null || movies.size() == 0){
            movie_list.setVisibility(View.GONE);
            info.setVisibility(View.VISIBLE);
            info.setText("唔，什么都没有找到，试试换个词？");
        } else {
            movie_list.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;

            if(convertView == null){
                LayoutInflater laInflater = (LayoutInflater) parent.getContext()
                        .getSystemService(android.app.Service.LAYOUT_INFLATER_SERVICE);
                convertView = laInflater.inflate(R.layout.item_movie, null);
                holder = new Holder();
                holder.movie_pic = (ImageView) convertView.findViewById(R.id.movie_pic);
                holder.movie_time = (TextView) convertView.findViewById(R.id.movie_time);
                holder.movie_name = (TextView) convertView.findViewById(R.id.movie_name);
                holder.movie_summary = (TextView) convertView.findViewById(R.id.movie_summary);
                holder.movie_looks = (TextView) convertView.findViewById(R.id.mem_delete);
                convertView.setTag(holder);

                if(picWidth == 0){
                    holder.movie_pic.measure(0, 0);
                    picWidth = holder.movie_pic.getMeasuredWidth();
                    picHeight = picWidth * 14 / 25;
                }
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.movie_name.setText("" + movies.get(position).getTitle());
            holder.movie_pic.setImageResource(R.mipmap.ic_movie_default);
            if(!StringHandler.isEmpty(movies.get(position).getThumbnail())){
                Picasso.with(MoviesActivity.this).load(movies.get(position).getThumbnail())
                        .resize(picWidth, picHeight).into(holder.movie_pic);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            if(movies == null)
                return 0;
            return movies.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
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

    public void login(){
        if(!session.contains("EMClient.login")){
            EMClient.getInstance().login(user.getHx_user().getUsername(),
                    user.getHx_user().getPassword(), new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    session.put("EMClient.login", true);
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }

                        @Override
                        public void onError(int code, String message) {
                            asyncToast("连接服务器失败");
                        }
                    });
        }
    }

    public void sendMovie(){
        if(session.contains("robotBeans")){
            List<RobotBean> robotBeans =
                    (List<RobotBean>)session.get("robotBeans");
            RobotBean robot = robotBeans.get(0);
            String pk = spt.getString(SptConfig.ROBOT_KEY, null);
            if(pk != null){
                int len = robotBeans.size();
                for (int i = 0; i < len; i++){
                    if(pk.equals(robotBeans.get(i).getId() + "")){
                        robot = robotBeans.get(i);
                        break;
                    }
                }
            }
            EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
            cmdMsg.setReceipt(robot.getHx_username());
            cmdMsg.addBody(new EMCmdMessageBody("{\"data\":\"" + vid + "\", \"type\":3}"));
            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
            if(vid.equals("stop")){
                showMessage("已经发送停止命令，请稍等");
            } else {
                showMessage("已经发送给" + robot.getName() + "，请稍等");
            }
        }
    }

    public static class Holder {
        public ImageView movie_pic;
        public TextView movie_time;
        public TextView movie_name;
        public TextView movie_summary;
        public TextView movie_looks;
    }
}
