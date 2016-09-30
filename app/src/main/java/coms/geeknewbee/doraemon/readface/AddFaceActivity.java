package coms.geeknewbee.doraemon.readface;

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
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.communicate.socket.SocketManager;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.readface.bean.ReadFaceInitParams;
import coms.geeknewbee.doraemon.robot.RobotActivity;
import coms.geeknewbee.doraemon.utils.ILog;
import dou.utils.BitmapUtil;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

/**
 * Created by GYY on 2016/9/30.
 */
public class AddFaceActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_open;
    private Button btn_ok;
    private EditText et_name;

    private YMFaceTrack faceTrack;
    private SocketManager socketManager;

    private boolean isSuccess;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            if (message == null) {
                ILog.e("socket连接已断开");
                Toast.makeText(AddFaceActivity.this, "socket连接已断开", Toast.LENGTH_SHORT);
                socketManager.close();
                Intent intent = new Intent(AddFaceActivity.this, RobotActivity.class);
                startActivity(intent);
                finish();
            }else if (message.startsWith(2 + "")) {
                String code2 = 2 + "";
                String data = message.substring(message.length() - code2.length());
                if (data.equals("1")) {
                    ILog.e("可以录入");
                } else {
                    ILog.e("不可以录入");
                }
            } else if (message.startsWith(5 + "")) {
                String code5 = 5 + "";
                String data = message.substring(message.length() - code5.length());
                if (data.equals("1")) {
                    isSuccess = true;
                    ILog.e("添加成功");
                    Toast.makeText(AddFaceActivity.this, "照片添加成功，请继续录入人名！", Toast.LENGTH_SHORT).show();
                } else {
                    ILog.e("添加失败");
                    Toast.makeText(AddFaceActivity.this, "照片添加失败，请重新选取照片进行添加！", Toast.LENGTH_SHORT).show();
                }
            } else if (message.startsWith(4 + "")) {
                String code4 = 4 + "";
                String data = message.substring(message.length() - code4.length());
                if (data.equals("1")) {
                    ILog.e("添加成功");
                    Toast.makeText(AddFaceActivity.this, "录入成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        et_name = (EditText) findViewById(R.id.et_name);

        btn_open.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        socketManager = SocketManager.getInstance();
        socketManager.init(handler, this);

        faceTrack = new YMFaceTrack();
        faceTrack.initTrack(this, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_1920);
        faceTrack.setRecognitionConfidence(80);
        faceTrack.resetAlbum();

        isSuccess = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open: //打开图库
                //发送相机参数信息
                ReadFaceInitParams initParams = new ReadFaceInitParams(YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_1920, 0, 0);
                Gson gson = new Gson();
                String json = gson.toJson(initParams);
                String send = GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET + GlobalContants.READY_ADD_FACE
                        + json + GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET;
                socketManager.writeInfo(send.getBytes(), 2);
                //打开图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
                break;
            case R.id.btn_send:
                String name = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "名字不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //  如果收到图片发送成功才可以发送
                if (isSuccess) {
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
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                Bitmap bitmap = BitmapUtil.decodeScaleImage(path, 640, 640);
                List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);

                if (ymFaces != null && ymFaces.size() != 0) {
                    Toast.makeText(this, "识别到人脸信息，正在进行添加", Toast.LENGTH_SHORT).show();

                    ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                    bitmap.recycle();//自由选择是否进行回收
                    byte[] bytes = output.toByteArray();//转换成功了
                    try {
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    addFace(bytes);
                } else {
                    Toast.makeText(this, "未识别到人脸信息，请重新选取照片进行添加！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

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