package coms.geeknewbee.doraemon.index.center;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.index.center.presenter.EditProfilePresenter;
import coms.geeknewbee.doraemon.index.center.view.IEditProfileView;
import coms.geeknewbee.doraemon.utils.ClipPicture;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/4/7
 */
public class EditProfileActivity extends BaseActivity implements IEditProfileView {

    private ImageButton ibBack;
    private ImageView ivAratart;
    private EditText etNickname;
    private RadioButton rbFemale;
    private RadioButton rbMale;
    private RelativeLayout rlBith;
    private Button confirm;
    private TextView tvBirth;
    private ImageButton ibPhoto;
    private RadioButton rbXxx;
    private RadioGroup radioGroup;

    //请求码
    private static final int CODE_GALLERY_REQUEST = 0001;
    private static final int CODE_CAMERA_REQUEST = 0010;

    //自定义的弹出框类
    SelectPicPopupWindow menuWindow;

    private Bitmap mBitmap;
    private Uri imgUri;

    ClipPicture cp;

    private File picFile;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ivAratart = (ImageView) findViewById(R.id.iv_avatar);
        etNickname = (EditText) findViewById(R.id.et_nickname);
        rbFemale = (RadioButton) findViewById(R.id.rb_female);
        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rlBith = (RelativeLayout) findViewById(R.id.rl_bith);
        confirm = (Button) findViewById(R.id.confirm);
        tvBirth = (TextView) findViewById(R.id.tv_birth);
        ibPhoto = (ImageButton) findViewById(R.id.ib_photo);
        rbXxx = (RadioButton) findViewById(R.id.rb_xxx);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
    }

    EditProfilePresenter presenter = new EditProfilePresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        assignViews();
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


    /**
     * 初始化监听器
     */
    private void initListener() {
        //返回
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        //底部弹出PopupWindow
        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(EditProfileActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(EditProfileActivity.this.findViewById(R.id.ll_center),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        //日历监听器
        rlBith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消重复
                SharePreferenceUtils.putString(EditProfileActivity.this,"birth",null);
                int year = 0;
                int month = 0;
                int day = 0;
                if(tvBirth.getText().toString().trim().length() == 0){
                    Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    try {
                        String s[] = tvBirth.getText().toString().trim().split("/");
                        year = Integer.parseInt(s[0]);
                        month = Integer.parseInt(s[1]) - 1;
                        day = Integer.parseInt(s[2]);
                    } catch (Exception e){
                        Calendar c = Calendar.getInstance();
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day = c.get(Calendar.DAY_OF_MONTH);
                    }
                }
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(EditProfileActivity.this,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                tvBirth.setText(year + "/" + (monthOfYear + 1)
                                        + "/" + dayOfMonth);
                            }
                        }
                        // 设置初始日期
                        , year, month, day).show();
            }
        });
        //确定键
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.createUser();
            }
        });

    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close_left, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            if(cp == null){
                cp = new ClipPicture(EditProfileActivity.this, null, ClipPicture.GET_BITMAP_AND_FILE) {
                    @Override
                    public void onFinish(Bitmap bitmap, File picture) {
                        ivAratart.setImageBitmap(bitmap);
                        picFile = picture;
                    }
                };
            }
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    cp.clipPicture(ClipPicture.USE_CAMERA);
                    break;
                case R.id.btn_pick_photo:
                    //从相册选择照片获取选中照片的uri
                    cp.clipPicture(ClipPicture.USE_PHOTO);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(cp != null){
            cp.dealPictureResult(requestCode, resultCode, data);
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    @Override
    public void goBack() {
        startActivity(new Intent(EditProfileActivity.this, CenterActivity.class));
        EditProfileActivity.this.finish();
    }

    @Override
    public String getNickName() {
        return etNickname.getText().toString().trim();
    }

    @Override
    public String getGender() {
        //2:男 3:女
        RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        String gender = radioButton.getText().toString().trim().substring(0, 1);
        if (gender.equals("女")) {
            return "3";
        } else if (gender.equals("男")) {
            return "2";
        } else {
            return "1";
        }
    }

    @Override
    public String getBirth() {
        String birth = tvBirth.getText().toString().trim();
        birth = birth.replaceAll("/", "-");
        return birth;
    }

    @Override
    public File getAvatar() {
        return picFile;
    }

    @Override
    public void showLoading() {
        showDialog("正在提交……");
    }

    @Override
    public void hideLoading() {
        hideDialog();
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public void setData(UserBean bean) {
        session.put(session.USER, bean);

        // avatar
        ivAratart.setImageResource(R.mipmap.avatar);
        if(!StringHandler.isEmpty(bean.getAvatar())){
            Picasso.with(EditProfileActivity.this).load(bean.getAvatar()).into(ivAratart);
        }

        //nickname
        if (bean.getNickname() == "") {
            etNickname.setText("");
        } else {
            etNickname.setText(bean.getNickname());
            SharePreferenceUtils.putString(EditProfileActivity.this, "nickname", bean.getNickname() + "  ");
        }

        //gender
        String gender = bean.getGender();
        if ("女".equals(gender)) {
            rbFemale.setChecked(true);
            SharePreferenceUtils.putString(EditProfileActivity.this, "gender", "3");
        } else if ("男".equals(gender)) {
            rbMale.setChecked(true);
            SharePreferenceUtils.putString(EditProfileActivity.this, "gender", "2");
        } else {
            rbXxx.setChecked(true);
            SharePreferenceUtils.putString(EditProfileActivity.this, "gender", "1");
        }

        //birth
        if (bean.getBirthday() == null) {
            tvBirth.setText("");
        } else {
            tvBirth.setText("  " + bean.getBirthday().toString());
            SharePreferenceUtils.putString(EditProfileActivity.this, "birth", "  " + bean.getBirthday().toString().replace('-', '/'));
        }
    }

    @Override
    public void updateUser(UserBean ub) {
        UserBean user = (UserBean)session.get(session.USER);
        // TODO 修改用户信息
        user.setBirthday(ub.getBirthday());
        user.setNickname(ub.getNickname());
        user.setAvatar(ub.getAvatar());
        picFile = null;
    }

    @Override
    public void showMessage(String msg) {
        showMsg("系统提示", msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }
}
