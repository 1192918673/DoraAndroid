package coms.geeknewbee.doraemon.robot.readface;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.communicate.socket.SocketManager;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.robot.readface.bean.ReadFaceInitParams;
import coms.geeknewbee.doraemon.robot.RobotActivity;
import coms.geeknewbee.doraemon.utils.ILog;
import dou.utils.BitmapUtil;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

/**
 * Created by GYY on 2016/9/30.
 */

public class AddFaceActivity extends BaseActivity implements View.OnClickListener {

    private final int timeout = 10000;
    private ImageButton ibBack;
    private Button btn_open;
    private Button btn_ok;
    private EditText et_name;

    private YMFaceTrack faceTrack;
    private SocketManager socketManager;

    private boolean isSuccess;

    /**
     * -----------------------使用socket返回的信息----------------------
     **/
    private final int MSG_WHAT_SOCKET_DISCONNECT = 1001;
    private final int MSG_WHAT_START_ADD_FACE = 1002;
    private final int MSG_WHAT_ADD_FACE = 1003;
    private final int MSG_WHAT_ADD_NAME = 1004;
    private final int MSG_WHAT_ADD_PHOTO = 1005;

    private boolean hasEnter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            removeCallbacks(finish);
            hideDialog();
            String data = (String) msg.obj;
            switch (msg.what) {
                case MSG_WHAT_SOCKET_DISCONNECT:    //socket连接断开
                    ILog.e("socket连接已断开");
                    Toast.makeText(AddFaceActivity.this, "socket连接已断开", Toast.LENGTH_SHORT);
                    socketManager.close();
                    Intent intent = new Intent(AddFaceActivity.this, RobotActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_WHAT_START_ADD_FACE:   //是否可以录入人脸
                    hasEnter = true;
                    if (data.equals("1")) {
                        ILog.e("可以录入");
                        Toast.makeText(AddFaceActivity.this, "可以开始录入人脸啦", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddFaceActivity.this, "稍等一下，多啦A梦还没有准备好哦！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                case MSG_WHAT_ADD_PHOTO:    //照片是否添加成功
                    if (data.equals("1")) {
                        isSuccess = true;
                        ILog.e("照片添加成功");
                        Toast.makeText(AddFaceActivity.this, "照片添加成功，请继续录入人名！", Toast.LENGTH_SHORT).show();
                    } else {
                        ILog.e("照片添加失败");
                        Toast.makeText(AddFaceActivity.this, "照片添加失败，请重新选取照片进行添加！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_WHAT_ADD_NAME: //人名是否添加成功
                    if (data.equals("1")) {
                        ILog.e("人名添加成功");
                        Toast.makeText(AddFaceActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        ILog.e("人名添加失败");
                        Toast.makeText(AddFaceActivity.this, "人名添加失败，请再次发送！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    Runnable finish = new Runnable() {
        @Override
        public void run() {
            hideDialog();
            if (!hasEnter) {
                Toast.makeText(AddFaceActivity.this, "发送超时，请重新进入", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddFaceActivity.this, "发送超时，请重新添加", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        et_name = (EditText) findViewById(R.id.et_name);

        ibBack.setOnClickListener(this);
        btn_open.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        socketManager = SocketManager.getInstance();
        socketManager.init(handler, this);

        faceTrack = new YMFaceTrack();
        faceTrack.initTrack(this, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_1920);
        faceTrack.setRecognitionConfidence(80);
        faceTrack.resetAlbum();

        if (!hasEnter) {
            handler.postDelayed(finish, timeout);
            showDialog("正在通知哆啦A梦要添加人脸");
            //发送相机参数信息
            ReadFaceInitParams initParams = new ReadFaceInitParams(YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_1920, 0, 0);
            Gson gson = new Gson();
            String json = gson.toJson(initParams);
            String send = GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET + GlobalContants.READY_ADD_FACE
                    + json + GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET;
            socketManager.writeInfo(send.getBytes(), 2);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.btn_open: //打开图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
                break;
            case R.id.btn_ok:
                String name = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "名字不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //  如果收到图片发送成功才可以发送
                if (!isSuccess) {
                    Toast.makeText(AddFaceActivity.this, "请先选取照片再进行添加！", Toast.LENGTH_SHORT).show();
                } else {
                    handler.postDelayed(finish, timeout);
                    showDialog("正在发送人名信息");
                    String data = GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET + GlobalContants.NAME_DATA + name + GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET;
                    ILog.e("发送人名信息：" + data);
                    socketManager.writeInfo(data.getBytes(), 2);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            isSuccess = false;
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Bitmap bitmap = BitmapUtil.decodeScaleImage(path, 640, 640);
//                List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);
                //压缩图片
                Bitmap newBitmap = BitmapUtil.scaleImage(bitmap, 0.4f, 0.4f);
                //进行人脸识别
                List<YMFace> ymFaces = faceTrack.detectMultiBitmap(newBitmap);
                //识别成功
                if (ymFaces != null && ymFaces.size() != 0) {
                    Toast.makeText(this, "识别到人脸信息，正在进行添加", Toast.LENGTH_SHORT).show();
//                    float[] rect = ymFaces.get(0).getRect();
//                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, (int) Math.ceil(rect[0] / 2), (int) Math.ceil(rect[1] / 3),
//                            (int) Math.ceil(rect[2] + rect[0]), (int) Math.ceil(rect[3] + rect[1]));
//                    try {
//                        FileOutputStream fos = new FileOutputStream("/storage/emulated/0/DCIM/Camera/1.jpg");
//                        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                    newBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                    bitmap.recycle();//自由选择是否进行回收
                    newBitmap.recycle();
                    byte[] bytes = output.toByteArray();//转换成功了
                    try {
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(finish, timeout);
                    showDialog("正在发送照片");
                    //发送人脸信息
                    addFace(bytes);
                } else {
                    Toast.makeText(this, "未识别到人脸信息，请重新选取照片进行添加！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 发送人脸信息
     *
     * @param bytes 照片字节数组
     */
    private void addFace(byte[] bytes) {
        byte[] prefix = GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET.getBytes();
        byte[] suffix = GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET.getBytes();
        byte[] code = new byte[]{0x35};

        byte[] faceInfo = new byte[prefix.length + suffix.length + code.length + bytes.length];
        System.arraycopy(prefix, 0, faceInfo, 0, prefix.length);
        System.arraycopy(code, 0, faceInfo, prefix.length, code.length);
        System.arraycopy(bytes, 0, faceInfo, prefix.length + code.length, bytes.length);
        System.arraycopy(suffix, 0, faceInfo, prefix.length + code.length + bytes.length, suffix.length);
        ILog.e("发送人脸信息：" + faceInfo);
        socketManager.writeInfo(faceInfo, 2);
    }
}