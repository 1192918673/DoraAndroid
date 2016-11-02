package coms.geeknewbee.doraemon.robot.readface;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.communicate.socket.SocketManager;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.robot.readface.bean.ReadFaceInitParams;
import coms.geeknewbee.doraemon.robot.RobotActivity;
import coms.geeknewbee.doraemon.utils.ILog;
import dou.utils.BitmapUtil;
import dou.utils.DLog;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

/**
 * Created by mac on 16/8/11.
 */
public class InsertFaceActivity extends FaceBaseActivity {

    int addCount = -1;

    private TextView tips, tips_load;
    private View show_image;
    private Button add_face;

    private SocketManager socketManager;
    private ProgressDialog pd;
    //  发送准备信息的超时时间30s
    private long OVERTIME = 30000;
    private boolean isEnter;
    //当前是否在发送人脸信息
    private boolean isSending;
    //是否保存图片
    private boolean isSaveImg = false;

    /**
     * -----------------------使用socket返回的信息----------------------
     **/
    private final int MSG_WHAT_SOCKET_DISCONNECT = 1001;
    private final int MSG_WHAT_START_ADD_FACE = 1002;
    private final int MSG_WHAT_ADD_FACE = 1003;
    private final int MSG_WHAT_ADD_NAME = 1004;
    private final int MSG_WHAT_ADD_PHOTO = 1005;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideDialog();
            handler.removeCallbacks(finish);
            String data = (String) msg.obj;
            switch (msg.what) {
                case MSG_WHAT_SOCKET_DISCONNECT:    //socket连接断开
                    ILog.e("socket连接已断开");
                    Toast.makeText(InsertFaceActivity.this, "socket连接已断开", Toast.LENGTH_SHORT);
                    socketManager.close();
                    Intent intent = new Intent(InsertFaceActivity.this, RobotActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_WHAT_START_ADD_FACE:   //是否可以录入人脸
                    isEnter = false;
                    if (data.equals("1")) {
                        addCount++;
                        ILog.e("可以录入");
                        Toast.makeText(InsertFaceActivity.this, "可以开始录入人脸啦", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(InsertFaceActivity.this, "稍等一下，多啦A梦还没有准备好哦！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                case MSG_WHAT_ADD_PHOTO: //人脸是否添加成功
                    if (data.equals("1")) {
                        addCount++;
                        switch (addCount) {
                            case 1: //第一张人脸添加完成
                                tips_load.setText("准备添加第二张人脸--侧脸(2/4)");
                                show_image.setBackgroundResource(R.drawable.nomal_2);
                                break;
                            case 2: //第二张人脸添加完成
                                tips_load.setText("准备添加第三张人脸--抬头侧脸(3/4)");
                                show_image.setBackgroundResource(R.drawable.nomal_3);
                                break;
                            case 3: //第三张人脸添加完成
                                tips_load.setText("准备添加第四张人脸--低头侧脸(4/4)");
                                show_image.setBackgroundResource(R.drawable.nomal_4);
                                break;
                            case 4: //第四张人脸添加完成
                                doEnd();
                                break;

                        }
                    } else {
                        Toast.makeText(InsertFaceActivity.this, "添加失败，请重新添加", Toast.LENGTH_SHORT).show();
                    }
                    isSending = false;
                    break;
                case MSG_WHAT_ADD_NAME: //人名是否添加成功
                    if (data.equals("1")) {
                        addCount++;
                        AlertDialog.Builder builder = new AlertDialog.Builder(InsertFaceActivity.this);
                        builder.setCancelable(false);
                        builder.setMessage("当前录入完成，是否录入下一个？")
                                .setNegativeButton("是",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                addCount = 0;
                                                tips_load.setText("准备添加第一张人脸--正脸(1/4)");
                                                show_image.setBackgroundResource(R.drawable.nomal_1);
                                            }
                                        })
                                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        builder.create().show();
                    } else {
                        doEnd();
                        Toast.makeText(InsertFaceActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlock_insert_activity);
        setCamera_max_width(1280);

        socketManager = SocketManager.getInstance();
        socketManager.init(handler, this);

        initCamera();
        showFps(false);
        initView();
        i = 1;
        isEnter = true;
        showDialog("正在通知哆啦A梦要添加人脸");
        handler.postDelayed(finish, OVERTIME);
    }

    Runnable finish = new Runnable() {
        @Override
        public void run() {
            hideDialog();
            if (isEnter) {
                Toast.makeText(InsertFaceActivity.this, "发送超时，请重新进入", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(InsertFaceActivity.this, "发送超时，请重新添加", Toast.LENGTH_SHORT).show();
                isSending = false;
            }
        }
    };

    private void initView() {
        TextView title = (TextView) findViewById(R.id.page_title);
        View popView = findViewById(R.id.popView);
        tips = (TextView) findViewById(R.id.tips);
        tips_load = (TextView) findViewById(R.id.tips_load);
        show_image = findViewById(R.id.show_image);
        add_face = (Button) findViewById(R.id.add_face);
        show_image.setBackgroundResource(R.drawable.nomal_1);

        tips_load.setText("准备添加第一张人脸--正脸(1/4)");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) popView.getLayoutParams();
        params.width = sw;
        params.height = sw * 640 / 480;

        add_face.getLayoutParams().width = getDoomW(450);
        add_face.getLayoutParams().height = getDoomW(170);

        title.setText("录入人脸");
    }

    @Override
    protected void drawAnim(List<YMFace> faces, SurfaceView draw_view, float scale_bit, int cameraId, String fps) {
//        TrackUtil.drawAnim(faces, draw_view, scale_bit, cameraId, fps, false);
    }

    @Override
    protected List<YMFace> analyse(byte[] bytes, int iw, int ih) {
//        List<YMFace> faces = faceTrack.trackMulti(bytes, iw, ih);
        List<YMFace> faces = new ArrayList<>();
        YMFace face = faceTrack.track(bytes, iw, ih);
        if (face != null)
            faces.add(face);

        if (faces != null && faces.size() > 0) {
            initModel2(faces, bytes);
        } else {
            tipSetText("未检测到人脸");
        }
        return faces;
    }

    //相机检测后重新设置完参数将调用该方法
    @Override
    protected void startAddPerson() {
        //发送相机参数信息
        ReadFaceInitParams initParams = new ReadFaceInitParams(orientation, YMFaceTrack.RESIZE_WIDTH_640, iw, ih);
        Gson gson = new Gson();
        String json = gson.toJson(initParams);
        String data = GlobalContants.READY_ADD_FACE + json;
        socketManager.writeInfo(data.getBytes(), 2);
    }

    void tipSetText(final String string) {
        if (!tips.getText().toString().equals(string)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tips.setText(string);
                }
            });
        }
    }

    private void addFace(byte[] bytes) {
        byte[] code = new byte[]{0x35};
        byte[] send = new byte[code.length + bytes.length];
        System.arraycopy(code, 0, send, 0, code.length);
        System.arraycopy(bytes, 0, send, code.length, bytes.length);
        socketManager.writeInfo(send, 2);
    }

    private void cut(byte[] data, float[] rect) {
        isSending = true;
        //  将byte[]转为bitmap
        ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        Rect rect1 = new Rect(0, 0, iw, ih);
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, iw, ih, null);
        yuvimage.compressToJpeg(rect1, 100, outstr);
        Bitmap bitmap = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
        //  图片压缩
        Bitmap newBitmap = BitmapUtil.scaleImage(bitmap, 0.1f, 0.1f);
        //人脸识别
        List<YMFace> ymFaces = faceTrack.detectMultiBitmap(newBitmap);
        //识别成功
        if (ymFaces != null && ymFaces.size() != 0) {
            Toast.makeText(this, "识别到人脸信息，正在进行添加", Toast.LENGTH_SHORT).show();
            ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
            if (isSaveImg) {
                try {
                    FileOutputStream fos = new FileOutputStream("/storage/emulated/0/DCIM/Camera/" + i + ".jpg");
                    i++;
                    newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
            bitmap.recycle();
            newBitmap.recycle();//自由选择是否进行回收
            byte[] bytes = output.toByteArray();//转换成功了
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //发送人脸信息
            addFace(bytes);
        } else {
            isSending = false;
        }
    }

    private void initModel2(List<YMFace> faces, final byte[] bytes) {
        //rule 1 人脸框在指定框内
        if (!isCenter(faces.get(0).getRect())) {
            return;
        }
        // 获取人脸框的矩形区域
        float[] rect = faces.get(0).getRect();
        //rule 2 第一张图片是正脸的
        switch (addCount) {
            case 0:
                if (isAdd1(faces)) {
                    tipSetText("添加正脸");
                    if (!isSending) {
                        tipSetText("正在添加第一张人脸");
                        showDialog("正在添加第一张人脸");
                        handler.postDelayed(finish, OVERTIME);
                        cut(bytes, rect);
//                        addFace(bytes);
                    }
                } else {
                    tipSetText("请正脸面对");
                }
                break;
            case 1:
                if (isAdd2(faces)) {
                    tipSetText("添加侧脸数据");
                    if (!isSending) {
                        tipSetText("正在添加第二张人脸");
                        showDialog("正在添加第二张人脸");
                        handler.postDelayed(finish, OVERTIME);
                        cut(bytes, rect);
//                        addFace(bytes);
                    }
                } else {
                    tipSetText("请侧脸20°");
                }

                break;
            case 2:
                if (isAdd3(faces)) {
                    tipSetText("添加抬头数据");
                    if (!isSending) {
                        tipSetText("正在添加第三张人脸");
                        showDialog("正在添加第三张人脸");
                        handler.postDelayed(finish, OVERTIME);
                        cut(bytes, rect);
//                        addFace(bytes);
                    }
                } else {
                    tipSetText("请抬头20°");
                }
                break;
            case 3:
                if (isAdd4(faces)) {
                    tipSetText("添加低头数据");
                    if (!isSending) {
                        tipSetText("正在添加第四张人脸");
                        showDialog("正在添加第四张人脸");
                        handler.postDelayed(finish, OVERTIME);
                        cut(bytes, rect);
//                        addFace(bytes);
                    }
                } else {
                    tipSetText("请低头20°");
                }
                break;
        }
    }

    private boolean isAdd4(List<YMFace> faces) {//加低头数据

        YMFace face = faces.get(0);
        float facialOri[] = face.getHeadpose();
        float y = facialOri[1];

        DLog.d(" ori " + y);
        if (y > 0) {
            return true;
        }
        return false;
    }

    private boolean isAdd3(List<YMFace> faces) {//加抬头数据

        YMFace face = faces.get(0);
        float facialOri[] = face.getHeadpose();
        float y = facialOri[1];

        if (y <= -10) {
            return true;
        }
        return false;
    }

    private boolean isAdd2(List<YMFace> faces) {//加侧脸数据

        YMFace face = faces.get(0);
        float facialOri[] = face.getHeadpose();
        float z = facialOri[2];

        if (Math.abs(z) >= 1) {
            return true;
        }
        return false;
    }

    private boolean isCenter(float[] rect) {
//        float viewW = 480;
//        float viewH = 640;
//
//        float x = viewW - rect[0] - rect[2] / 2;
//        float y = rect[1] + rect[3] / 2;
//
//        float cenX = viewW / 2;
//        float cenY = viewH / 2;
//        float diff = 80;
//        if (rect[2] <= 150) {
//            //框太小
//            tipSetText("人脸太小，请离近点");
//            return false;
//        }
//
//        if (x >= (cenX - diff) && x <= (cenX + diff) && y >= (cenY - diff + 40) && y <= (cenY + diff + 40))
//            return true;
//        //位置未居中
//        tipSetText("请将人脸置于框内");
        return true;
    }

    private boolean isAdd1(List<YMFace> faces) {//加正脸数据

        YMFace face = faces.get(0);

        if (!isCenter(face.getRect())) {
            return false;
        }
        float facialOri[] = face.getHeadpose();

        float x = facialOri[0];
        float y = facialOri[1];
        float z = facialOri[2];

        if (Math.abs(x) <= 15 && Math.abs(y) <= 15 && Math.abs(z) <= 15) {
            return true;
        }
        return false;
    }

    public void topClick(View view) {
        switch (view.getId()) {
            case R.id.page_left:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (addCount >= 4) {
            stopCamera();
            finish();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("你当前共添加" + addCount + "张人脸数据，尚未注册完整，确定退出？")
                    .setNegativeButton("确定退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            faceTrack.deletePerson(personId);
                            stopCamera();
                            finish();
                        }
                    }).setPositiveButton("继续完善", null);
            builder.create().show();
        }
    }

    private AlertDialog dialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideDialog();
    }

    void doEnd() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        final EditText et = new EditText(this);
        et.setGravity(Gravity.CENTER);
        et.setHint("请输入昵称，不能为空哦");
        et.setHintTextColor(0xffc6c6c6);
        builder.setTitle("提示")
                .setMessage("人脸录入成功，请输入昵称 ")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = et.getText().toString().trim();
                        if (TextUtils.isEmpty(name)) {
                            doEnd();
                            return;
                        }
                        dialog.dismiss();
                        //  将人名信息发送
                        showDialog("正在发送人名信息");
                        String data = GlobalContants.NAME_DATA + name;
                        handler.postDelayed(finish, OVERTIME);
                        socketManager.writeInfo(data.getBytes(), 2);
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    public void showDialog(String message) {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setMessage(message);
            pd.setCancelable(false);
            pd.show();
        } else {
            pd.setMessage(message);
            pd.show();
        }
    }

    public void hideDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }
}
