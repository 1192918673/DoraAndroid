package coms.geeknewbee.doraemon.index.center;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.index.center.bean.APPBean;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.index.center.presenter.CenterPresenter;
import coms.geeknewbee.doraemon.index.center.view.ICenterView;
import coms.geeknewbee.doraemon.register_login.Splash2Activity;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;
import coms.geeknewbee.doraemon.utils.StringHandler;


/**
 * Created by chen on 2016/4/6
 */
public class CenterActivity extends BaseActivity implements ICenterView {

    private ImageButton ibBack;
    private TextView tvTitle;
    private ImageView ibAvatar;
    private TextView tvName;
    private TextView tvBirth;

    private RelativeLayout tvChangePhone;
    private RelativeLayout tvChangePassword;
    private RelativeLayout tvFeedback;
    private RelativeLayout tv_check;
    private RelativeLayout tvExit;

    private TextView tvProFile;
    private ImageView ivGender;
    private ImageView ivBirth;
    private ProgressBar progressBar;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ibAvatar = (ImageView) findViewById(R.id.ib_avatar);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvChangePhone = (RelativeLayout) findViewById(R.id.tv_change_phone);
        tvChangePassword = (RelativeLayout) findViewById(R.id.tv_change_password);
        tvFeedback = (RelativeLayout) findViewById(R.id.tv_feedback);
        tvExit = (RelativeLayout) findViewById(R.id.tv_exit);
        tv_check = (RelativeLayout) findViewById(R.id.tv_check);
        tvProFile = (TextView) findViewById(R.id.edit_profile);
        tvBirth = (TextView) findViewById(R.id.tv_birth);
        ivGender = (ImageView) findViewById(R.id.iv_gender);
        ivBirth = (ImageView) findViewById(R.id.iv_birth);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    CenterPresenter presenter = new CenterPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        assignViews();
        //从本地获取
        initListener();
        if(session.contains(session.USER)){
            UserBean user = (UserBean)session.get(session.USER);
            if(user == null){
                presenter.getUser();
            } else {
                setData(user);
            }
        } else {
            presenter.getUser();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(session.contains(session.USER)){
            UserBean user = (UserBean)session.get(session.USER);
            if(user != null){
                setData(user);
            }
        }
    }

    private void initListener() {
        ibBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        tvExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //清理用户信息
                spt.putString(SptConfig.LOGIN_TOKEN, null);
                session.removeAll();
                //退回到登录页面
                session.closeAllActivities();
                Intent intent = new Intent(getApplication(), Splash2Activity.class);
                startActivity(intent);
            }
        });
        tvProFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CenterActivity.this, EditProfileActivity.class));
            }
        });
        //更换手机号
        tvChangePhone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CenterActivity.this, EditMobileActivity.class));
            }
        });
        //修改密码
        tvChangePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CenterActivity.this, UserUpdatePwd.class));
            }
        });
        tvFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CenterActivity.this, SuggestionsActivity.class));
            }
        });
        tv_check.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("正在检测应用版本信息……");
                presenter.getAppVersion();
            }
        });
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close_left, 0);
    }

    @Override
    public void goBack() {
        startActivity(new Intent(CenterActivity.this, IndexActivity.class));
        CenterActivity.this.finish();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public void setData(final UserBean bean) {
        session.put(session.USER, bean);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // avatar
                ibAvatar.setImageResource(R.mipmap.avatar);
                if(!StringHandler.isEmpty(bean.getAvatar())){
                    Picasso.with(CenterActivity.this).load(bean.getAvatar()).into(ibAvatar);
                }

                //nickname
                if (bean.getNickname() == "") {
                    tvName.setVisibility(View.INVISIBLE);
                } else {
                    tvName.setText(bean.getNickname() + "  ");
                    SharePreferenceUtils.putString(MyApplication.getContext(),"nickname",bean.getNickname() + "  ");
                }

                //gender  SharePre// 3女 2男 1未知
                String gender = bean.getGender();
                if ("女".equals(gender)) {
                    ivGender.setBackgroundResource(R.mipmap.female);
                    SharePreferenceUtils.putString(MyApplication.getContext(),"gender","3");
                } else if ("男".equals(gender)) {
                    ivGender.setBackgroundResource(R.mipmap.male);
                    SharePreferenceUtils.putString(MyApplication.getContext(), "gender", "2");
                } else {
                    ivGender.setVisibility(View.INVISIBLE);
                    SharePreferenceUtils.putString(MyApplication.getContext(), "gender", "1");
                }

                //birth
                if (bean.getBirthday() == null) {
                    tvBirth.setText("");
                    ivBirth.setVisibility(View.INVISIBLE);
                } else {
                    tvBirth.setText("  " + bean.getBirthday().toString().replace('-', '/'));
                    SharePreferenceUtils.putString(MyApplication.getContext(),"birth",
                            "  " + bean.getBirthday().toString().replace('-', '/'));
                }
            }
        });
    }

    @Override
    public void setAPP(final APPBean app) {
        hideDialog();
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            if(app == null || app.getApk() == null || app.getLast_version_code() <= version){
                showMsg("系统提示", "当前已经是最新版本！");
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle("" + "系统提示").setMessage("本应用有最新版本，是否立即更新？")
                        .setPositiveButton("更新", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                tt.showMessage("已经开始下载，请稍等……", tt.SHORT);
                                presenter.download(app.getApk());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
    }
}
