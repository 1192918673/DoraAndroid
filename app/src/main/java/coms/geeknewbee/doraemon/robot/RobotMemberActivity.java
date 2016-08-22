package coms.geeknewbee.doraemon.robot;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotMemberPresenter;
import coms.geeknewbee.doraemon.robot.view.IMemberView;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/4/6
 */
public class RobotMemberActivity extends BaseActivity implements IMemberView {

    /**---------------------组件--------------------**/
    private ImageButton ibBack;
    private ImageView drAvatar;
    private TextView tv_edit;
    private ListView userList;

    RelativeLayout layout_add;

    EditText tv_mobile;

    TextView tv_ok;

    TextView tv_cancel;

    /**---------------------数据--------------------**/
    List<UserBean> users;

    UserBean user;

    boolean editing = false;

    String robotPk;

    IRobotMemberPresenter presenter;

    Map<Integer, OnClickListener> roleListener = new HashMap<Integer, OnClickListener>();

    Map<Integer, OnClickListener> removeListener = new HashMap<Integer, OnClickListener>();

    Drawable female;

    Drawable male;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        drAvatar = (ImageView) findViewById(R.id.dr_avatar);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        userList = (ListView)findViewById(R.id.userList);

        layout_add = (RelativeLayout)findViewById(R.id.layout_add);
        tv_mobile = (EditText) findViewById(R.id.tv_mobile);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);

        tv_edit.setVisibility(View.GONE);
        userList.setAdapter(adapter);
        presenter = new IRobotMemberPresenter(this);
        skm = new SoftKeyboardManager(tv_mobile);

        female = getResources().getDrawable(R.mipmap.female);
        female.setBounds(0, 0, female.getMinimumWidth(), female.getMinimumHeight());
        male = getResources().getDrawable(R.mipmap.male);
        male.setBounds(0, 0, male.getMinimumWidth(), male.getMinimumHeight());

        robotPk = getIntent().getStringExtra("robotPk");

        showDialog("正在加载成员信息……");
        if(session.contains(session.USER)){
            UserBean user = (UserBean)session.get(session.USER);
            if(user == null){
                presenter.getUser();
            } else {
                setUser(user);
            }
        } else {
            presenter.getUser();
        }
        presenter.getMembers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_member);
        assignViews();
        initListener();
    }

    private void initListener() {
        ibBack.setOnClickListener(clickListener);
        tv_edit.setOnClickListener(clickListener);
        drAvatar.setOnClickListener(clickListener);
        layout_add.setOnClickListener(clickListener);
        tv_ok.setOnClickListener(clickListener);
        tv_cancel.setOnClickListener(clickListener);
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_back:
                    finish();
                    break;
                case R.id.tv_edit:
                    editing = !editing;
                    if(editing){
                        drAvatar.setImageResource(R.mipmap.cancel);
                        tv_edit.setText("取消");
                    } else {
                        drAvatar.setImageResource(R.mipmap.add_talk);
                        tv_edit.setText("编辑");
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.dr_avatar:
                    if(users == null || user.getRole() == null || !user.getRole().equals("1")){
                        showMessage("只有管理员才能邀请家庭成员！");
                        return;
                    }
                    if(!editing){
                        layout_add.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.layout_add:
                case R.id.tv_cancel:
                    layout_add.setVisibility(View.GONE);
                    break;
                case R.id.tv_ok:
                    layout_add.setVisibility(View.GONE);
                    showDialog("正在邀请成员……");
                    presenter.inviteMember();
                    break;
            }
        }
    };

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;

            if(convertView == null){
                LayoutInflater laInflater = (LayoutInflater) parent.getContext()
                        .getSystemService(android.app.Service.LAYOUT_INFLATER_SERVICE);
                convertView = laInflater.inflate(R.layout.item_robot_member, null);
                holder = new Holder();
                holder.mem_portrait = (ImageView) convertView.findViewById(R.id.mem_portrait);
                holder.mem_name = (TextView) convertView.findViewById(R.id.mem_name);
                holder.mem_role = (TextView) convertView.findViewById(R.id.mem_role);
                holder.mem_option = (TextView) convertView.findViewById(R.id.mem_option);
                holder.mem_delete = (TextView) convertView.findViewById(R.id.mem_delete);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.mem_name.setText(users.get(position).getNickname() + " ");
            holder.mem_portrait.setImageResource(R.mipmap.avatar);
            if(!StringHandler.isEmpty(users.get(position).getAvatar())){
                Picasso.with(RobotMemberActivity.this).load(users.get(position)
                        .getAvatar()).into(holder.mem_portrait);
            }
            if(users.get(position).getRole().equals("0")){
                holder.mem_role.setText("普通成员");
            } else if(users.get(position).getRole().equals("1")){
                holder.mem_role.setText("管理员");
            } else {
                holder.mem_role.setText("普通成员");
            }
            if(StringHandler.isEmpty(users.get(position).getGender())){
                holder.mem_name.setCompoundDrawables(null, null, null, null);
            } else if(users.get(position).getGender().equals("男")){
                holder.mem_name.setCompoundDrawables(null, null, male, null);
            } else if(users.get(position).getGender().equals("女")){
                holder.mem_name.setCompoundDrawables(null, null, female, null);
            }
            if(editing && (user == null || user.getId() != users.get(position).getId())){
                holder.mem_delete.setVisibility(View.VISIBLE);
                if(!users.get(position).getRole().equals("1")){
                    holder.mem_option.setVisibility(View.VISIBLE);
                    if(!roleListener.containsKey((Integer)position)){
                        OnClickListener listener = new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("正在提交设置……");
                                presenter.optionMember(position);
                            }
                        };
                        roleListener.put(position, listener);
                    }
                    holder.mem_option.setOnClickListener(roleListener.get((Integer)position));
                }
                if(!removeListener.containsKey((Integer)position)){
                    OnClickListener listener = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("正在提交设置……");
                            presenter.removeMember(position);
                        }
                    };
                    removeListener.put(position, listener);
                }
                holder.mem_delete.setOnClickListener(removeListener.get((Integer)position));
            } else {
                holder.mem_option.setVisibility(View.GONE);
                holder.mem_delete.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            if(users == null){
                return 0;
            }
            return users.size();
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

    public String getToken(){
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    public String getRobotPk(){
        return robotPk;
    }

    public String getMobile(){
        return tv_mobile.getText().toString();
    }

    public String getUserPk(int position){
        if(users == null || users.size() <= position){
            return null;
        }
        return "" + users.get(position).getId();
    }

    public String getRole(int position){
        if(users == null || users.size() <= position){
            return null;
        }
        return "1";
    }

    public void setUser(UserBean ub){
        session.put(session.USER, ub);
        user = ub;
        adapter.notifyDataSetChanged();
        if(users != null){
            hideDialog();
            for (UserBean u: users) {
                ILog.e(u.getMobile() + " - " + u.getRole());
                if(u.getId() == user.getId()){
                    user.setRole(u.getRole());
                    if(user.getRole().equals("1")){
                        tv_edit.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public void setUserBeans(List<UserBean> userBeans){
        users = userBeans;
        adapter.notifyDataSetChanged();
        if(user != null){
            hideDialog();
            for (UserBean u: users) {
                ILog.e(u.getMobile() + " - " + u.getRole());
                if(u.getId() == user.getId()){
                    user.setRole(u.getRole());
                    if(user.getRole().equals("1")){
                        tv_edit.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void optionSuccess(int type, String pk) {
        if(type == 0){
            presenter.getMembers();
            hideDialog();
            showMessage("邀请成功");
        } else if(type == 1){
            for(UserBean ub : users){
                if(ub.getRole() != null && ub.getRole().equals("1")){
                    ub.setRole("0");
                }
                if(pk != null && pk.equals(ub.getId() + "")){
                    ub.setRole("1");
                }
            }
            user.setRole("0");
            editing = !editing;
            tv_edit.setVisibility(View.GONE);
            drAvatar.setImageResource(R.mipmap.add_talk);
            adapter.notifyDataSetChanged();
            showMessage("设置管理员成功");
        } else if(type == 2){
            for(UserBean ub : users){
                if(pk != null && pk.equals(ub.getId() + "")){
                    users.remove(ub);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
            showMessage("移除成功");
        }
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
    }

    @Override
    public void onBackPressed() {
        if(layout_add.getVisibility() == View.VISIBLE){
            layout_add.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public static class Holder {
        public ImageView mem_portrait;
        public TextView mem_name;
        public TextView mem_role;
        public TextView mem_option;
        public TextView mem_delete;
    }
}
