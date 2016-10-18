package coms.geeknewbee.doraemon.box.time_machine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.time_machine.presenter.IPhotoPresenter;
import coms.geeknewbee.doraemon.box.time_machine.view.IPhotoView;
import coms.geeknewbee.doraemon.entity.RobotPhoto;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ShowImageActivity extends BaseActivity implements IPhotoView {

    //    private ImageView imageView;
//    private int imageId;
//    private String dateKey;
    String robotPk;
    private Map<String, List<RobotPhoto>> photos;
    List<RobotPhoto> myPhotos;
    int num;


    IPhotoPresenter presenter;
    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_show_origin_image);
        vp = (ViewPager) findViewById(R.id.vp);
//        imageView = (ImageView) findViewById(R.id.imageView);

//        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);

//        String imageUrl = getIntent().getStringExtra("imageUrl");
//        imageId = getIntent().getIntExtra("imageId", 0);
//        dateKey = getIntent().getStringExtra("dateKey");
        robotPk = getIntent().getStringExtra("robotPk");
        myPhotos = (List<RobotPhoto>) getIntent().getSerializableExtra("photos");
        num = getIntent().getIntExtra("num", 0);
        vp.setAdapter(new myAdapter());
        vp.setCurrentItem(num);

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                num = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        presenter = new IPhotoPresenter(this);

//        Picasso.with(this).load(imageUrl).into(imageView, new Callback() {
//            @Override
//            public void onSuccess() {
//                ILog.e("test", "succ");
//                mAttacher.update();
//                mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
//                    @Override
//                    public void onViewTap(View view, float v, float v1) {
//                        finish();
//                    }
//                });
//            }
//
//            @Override
//            public void onError() {
//                ILog.e("test", "fail");
//            }
//        });

    }

    public void onBackClick(View v) {
        finish();
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
    public String getIds() {
        return "" + myPhotos.get(num).getId();
    }

    @Override
    public int getPage() {
        return 0;
    }

    @Override
    public void setData(Map<String, List<RobotPhoto>> photos) {

    }

    @Override
    public void deleteSuccess() {
        hideDialog();
        myPhotos.remove(num);
        photos = (Map<String, List<RobotPhoto>>) session.get("tm_photos");
        for (String key : photos.keySet()) {
            if ((myPhotos.get(num).getDate_created()).contains(key)) {
                int len = photos.get(key).size();
                for (int i = 0; i < len; i++) {
                    if (photos.get(key).get(i).getId() == myPhotos.get(num).getId()) {
                        photos.get(key).remove(i);
                        break;
                    }
                }
                if (photos.get(key).size() == 0) {
                    photos.remove(key);
                }
                break;
            }
        }
        session.put("pic_refresh", true);
        finish();
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

    public void onDeleteClick(View v) {
        new AlertDialog.Builder(this)
                .setTitle("确定要删除这张照片吗？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showDialog("正在删除图片……");
                                presenter.removePhoto();
                            }
                        }).setNegativeButton("取消", null).create()
                .show();
    }

    private class myAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return myPhotos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(ShowImageActivity.this);
            Picasso.with(ShowImageActivity.this)
                    .load(myPhotos.get(position).getPhoto())
                    .into(view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
