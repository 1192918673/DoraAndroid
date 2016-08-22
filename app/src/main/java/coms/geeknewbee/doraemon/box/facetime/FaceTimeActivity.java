package coms.geeknewbee.doraemon.box.facetime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;

import java.io.File;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.facetime.presenter.IUserPresenter;
import coms.geeknewbee.doraemon.box.facetime.view.IUserView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.utils.FileHandler;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chensiyuan on 2016/5/10.
 * Desc：实时通话模块，建立通话状态监听，发起通话。 环信username暂时写死，待用Retrofit网络请求获取username。
 */
public class FaceTimeActivity extends BaseActivity implements IUserView {

    /**----------------------布局组件---------------------**/

    TextView info;
    ImageView closeVoice;
    ImageView closeTalk;
    ImageView getPic;
    ImageButton ib_back;

    View video_bg;
    LinearLayout panel;
    RelativeLayout top;

    LinearLayout llShowControl;

    RelativeLayout rlShowControl;

    RelativeLayout rlInput;

    TextView tvShowControl;

    ImageView ivShowUp;

    ImageView ivShowCenter;

    ImageView ivShowDown;

    ImageView ivShowLeft;

    ImageView ivShowRight;

    PowerManager.WakeLock wakeLock;

    EditText etSend;

    TextView tvSend;

    TextView tvSendVoice;

    /**----------------------用户登录数据---------------------**/

    UserBean user;

    IUserPresenter presenter;

    /**----------------------聊天数据---------------------**/

    private EMLocalSurfaceView localSurface;

    private EMOppositeSurfaceView oppositeSurface;

    private EMCallManager.EMVideoCallHelper callHelper;

    /**----------------------声音控制数据---------------------**/

    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected int outgoing;
    private int streamID;

    boolean volume = true;
    int vol = 0;
    int voiceType = AudioManager.STREAM_VOICE_CALL;

    /**----------------------截图文件操作数据---------------------**/

    String showAction[] = {"head_left", "head_right", "head_up", "head_down", "head_front"};

    final static int LEFT = 0;

    final static int RIGHT = 1;

    final static int UP = 2;

    final static int DOWN = 3;

    final static int CENTER = 4;

    FileHandler fh;

    String picFile;

    String robotPk;

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facetime);

        // TODO 第一步，初始化数据
        initialize();

        handler.postDelayed(startLoading, 200);
    }

    Runnable startLoading = new Runnable() {
        @Override
        public void run() {
            // TODO 第二步，登录聊天服务器
            playMakeCallSounds();
            presenter = new IUserPresenter(FaceTimeActivity.this);

            if(session.contains(session.USER)){
                user = (UserBean)session.get(session.USER);
                if(user == null){
                    presenter.getUser();
                } else {
                    login();
                }
            } else {
                presenter.getUser();
            }
        }
    };

    private void initialize() {
        // TODO 布局数据
        localSurface = (EMLocalSurfaceView) findViewById(R.id.localSurface);
        oppositeSurface = (EMOppositeSurfaceView) findViewById(R.id.oppositeSurface);
        info = (TextView)findViewById(R.id.info);
        closeVoice = (ImageView) findViewById(R.id.close_voice);
        closeTalk = (ImageView) findViewById(R.id.close_talk);

        getPic = (ImageView) findViewById(R.id.get_pic);
        video_bg = findViewById(R.id.video_bg);
        panel = (LinearLayout) findViewById(R.id.panel);
        top = (RelativeLayout) findViewById(R.id.top);
        ib_back = (ImageButton)findViewById(R.id.ib_back);

        llShowControl = (LinearLayout) findViewById(R.id.llShowControl);
        rlShowControl = (RelativeLayout) findViewById(R.id.rlShowControl);
        tvShowControl = (TextView)findViewById(R.id.tvShowControl);
        ivShowUp = (ImageView) findViewById(R.id.ivShowUp);
        ivShowDown = (ImageView) findViewById(R.id.ivShowDown);

        ivShowCenter = (ImageView) findViewById(R.id.ivShowCenter);
        ivShowLeft = (ImageView) findViewById(R.id.ivShowLeft);
        ivShowRight = (ImageView) findViewById(R.id.ivShowRight);
        rlInput = (RelativeLayout) findViewById(R.id.rlInput);
        etSend = (EditText) findViewById(R.id.etSend);

        tvSend = (TextView) findViewById(R.id.tvSend);
        tvSendVoice = (TextView) findViewById(R.id.tvSendVoice);

        localSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        localSurface.setZOrderOnTop(true);
        localSurface.setVisibility(View.INVISIBLE);

//        int width = (Integer) session.get(Session.WIDTH);
//        localSurface.getLayoutParams().width = width / 3;
//        localSurface.getLayoutParams().height = width * 4 / 9;

        robotPk = getIntent().getStringExtra("robotPk");
        fh = new FileHandler(this);
        //环信视频通话需要设置显示自己和对方的surfaceView
        EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);

        // TODO 监听事件
        closeVoice.setOnClickListener(clickListener);
        closeTalk.setOnClickListener(clickListener);
        getPic.setOnClickListener(clickListener);
        video_bg.setOnClickListener(clickListener);
        ib_back.setOnClickListener(clickListener);
        tvShowControl.setOnClickListener(clickListener);

        ivShowUp.setOnClickListener(clickListener);
        ivShowDown.setOnClickListener(clickListener);
        ivShowCenter.setOnClickListener(clickListener);
        ivShowLeft.setOnClickListener(clickListener);
        ivShowRight.setOnClickListener(clickListener);
        tvSend.setOnClickListener(clickListener);
        tvSendVoice.setOnClickListener(clickListener);

        // TODO 声音控制数据
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);
        openSpeakerOn();

        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
        outgoing = soundPool.load(this, R.raw.em_outgoing, 1);
        info.setText("正在连接服务器……");

        oppositeSurface.getHolder().lockCanvas();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
    }

    @Override
    public String getRobotPk() {
        return robotPk;
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public void setUser(UserBean user) {
        this.user = user;
        session.put(Session.USER, user);
        login();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int width = (Integer) session.get(Session.WIDTH);
        oppositeSurface.getLayoutParams().height = width - 2;
        wakeLock.acquire();
    }

    //登陆
    public void login() {
        if(session.contains("EMClient.login")){
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            handler.postDelayed(link, 200);
        } else {
            EMClient.getInstance().login(user.getHx_user().getUsername(),
                    user.getHx_user().getPassword(), new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    session.put("EMClient.login", true);
                                    EMClient.getInstance().groupManager().loadAllGroups();
                                    EMClient.getInstance().chatManager().loadAllConversations();
                                    handler.postDelayed(link, 200);
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

    Runnable link = new Runnable() {
        @Override
        public void run() {
            // TODO 第三步，开始初始化聊天数据
            callHelper = EMClient.getInstance().callManager().getVideoCallHelper();
            callHelper.setVideoBitrate(240);
            //callHelper.setVideoOrientation(EMLandscape);
            //状态监听
            callStateListener();
            //拨打视频通话
            faceTo();
        }
    };

    @Override
    public void asyncToast(String msg){
        Message m = new Message();
        m.what = 0;
        m.obj = msg;
        __h.sendMessage(m);
    }

    Handler __h = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String m = (String)msg.obj;
            info.setText("" + m);
            super.handleMessage(msg);
        }
    };

    /**
     * 挂断电话
     */
    private void shutDown() {
        //挂断电话
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.close_voice:
                    if(volume){
                        EMClient.getInstance().callManager().pauseVoiceTransfer();
                        closeVoice.setImageResource(R.mipmap.ic_video_voice_off);
                        vol = audioManager.getStreamVolume(voiceType);
                        audioManager.setStreamVolume(voiceType, 0, 0);
                    } else {
                        EMClient.getInstance().callManager().resumeVoiceTransfer();
                        closeVoice.setImageResource(R.mipmap.ic_video_voice_on);
                        audioManager.setStreamVolume(voiceType, vol, 0);
                    }
                    volume = !volume;
                    break;
                case R.id.close_talk:
                    shutDown();
                    break;
                case R.id.get_pic:
                    picFile = fh.getStoreName("pic", ".jpg");
                    callHelper.takePicture(picFile);
                    count = 10;
                    handler.postDelayed(upload, 100);
                    break;
                case R.id.video_bg:
                    if(panel.getVisibility() == View.VISIBLE){
                        panel.setVisibility(View.GONE);
                        top.setVisibility(View.GONE);
                        llShowControl.setVisibility(View.GONE);
                        rlInput.setVisibility(View.GONE);
                    } else {
                        panel.setVisibility(View.VISIBLE);
                        top.setVisibility(View.VISIBLE);
                        llShowControl.setVisibility(View.VISIBLE);
                        handler.postDelayed(hide, 5000);
                    }
                    break;
                case R.id.ib_back:
                    shutDown();
                    break;
                case R.id.tvShowControl:
                    handler.removeCallbacks(hide);
                    if(rlShowControl.getVisibility() == View.VISIBLE){
                        rlShowControl.setVisibility(View.GONE);
                        tvShowControl.setText("控制视角");
                    } else {
                        rlShowControl.setVisibility(View.VISIBLE);
                        tvShowControl.setText("收起控制");
                    }
                    handler.postDelayed(hide, 5000);
                    break;
                case R.id.ivShowUp:
                    handler.removeCallbacks(hide);
                    sendAction(UP);
                    handler.postDelayed(hide, 5000);
                    break;
                case R.id.ivShowDown:
                    handler.removeCallbacks(hide);
                    sendAction(DOWN);
                    handler.postDelayed(hide, 5000);
                    break;
                case R.id.ivShowLeft:
                    handler.removeCallbacks(hide);
                    sendAction(LEFT);
                    handler.postDelayed(hide, 5000);
                    break;
                case R.id.ivShowRight:
                    handler.removeCallbacks(hide);
                    sendAction(RIGHT);
                    handler.postDelayed(hide, 5000);
                    break;
                case R.id.ivShowCenter:
                    handler.removeCallbacks(hide);
                    sendAction(CENTER);
                    handler.postDelayed(hide, 5000);
                    break;
                case R.id.tvSend:
                    sendText();
                    break;
                case R.id.tvSendVoice:
                    handler.removeCallbacks(hide);
                    if(rlInput.getVisibility() == View.GONE){
                        llShowControl.setVisibility(View.GONE);
                        rlInput.setVisibility(View.VISIBLE);
                    } else {
                        llShowControl.setVisibility(View.VISIBLE);
                        rlInput.setVisibility(View.GONE);
                        handler.postDelayed(hide, 5000);
                    }
                    break;
            }
        }
    };


    /**
     * 通话状态监听
     */
    private void callStateListener() {
        EMClient.getInstance().callManager().addCallStateChangeListener(new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                Message msg = new Message();
                msg.obj = callState;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 拨打视频通话
     */
    private void faceTo() {
        try {
            //对方环信username robots接口
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
                EMClient.getInstance().callManager().makeVideoCall(robot.getHx_username());
                // EMClient.getInstance().callManager().makeVideoCall("f3b3262bec7a435cbdb8685e7a2cadac");
                info.setText("正在连接" + robot.getName() + "，请稍后");
            }
        } catch (EMServiceNotReadyException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EMCallStateChangeListener.CallState callState =
                    (EMCallStateChangeListener.CallState)msg.obj;
            switch (callState) {
                case CONNECTING: // 正在连接对方
                    ILog.e("环信通话状态监听：正在连接对方");
                    break;
                case CONNECTED: // 双方已经建立连接
                    ILog.e("环信通话状态监听：双方已建立连接");
                    info.setText("");
                    try {
                        closeVoice.setVisibility(View.VISIBLE);
                        llShowControl.setVisibility(View.VISIBLE);
                        getPic.setVisibility(View.VISIBLE);
                        tvShowControl.setVisibility(View.VISIBLE);
                        tvSendVoice.setVisibility(View.VISIBLE);
                        handler.postDelayed(hide, 5000);
                        video_bg.setBackgroundColor(0);
                        localSurface.setVisibility(View.VISIBLE);
                        if (soundPool != null){
                            soundPool.stop(streamID);
                            soundPool.release();
                        }
                    } catch (Exception e) {
                    }
                    break;
                case ACCEPTED: // 电话接通成功
                    ILog.e("环信通话状态监听：电话接通成功");
                    info.setText("");
                    break;
                case DISCONNNECTED: // 电话断了
                    ILog.e("环信通话状态监听：电话已挂断");
                    info.setText("");
                    shutDown();
                    tt.showMessage("通话已挂断……", tt.SHORT);
                    finish();
                    break;
                case NETWORK_UNSTABLE: //网络不稳定
                    ILog.e("环信通话状态监听：网络状态不稳定");
                    info.setText("呀，有干扰！通讯中断了");
                    video_bg.setBackgroundResource(R.mipmap.bg_star2);
                    localSurface.setVisibility(View.INVISIBLE);
                    shutDown();
                    break;
                case NETWORK_NORMAL: //网络恢复正常
                    ILog.e("环信通话状态监听：网络恢复正常");
                    video_bg.setBackgroundColor(0);
                    localSurface.setVisibility(View.INVISIBLE);
                    info.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    Runnable hide = new Runnable() {
        @Override
        public void run() {
            panel.setVisibility(View.GONE);
            top.setVisibility(View.GONE);
            llShowControl.setVisibility(View.GONE);
        }
    };

    // 打开扬声器
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    protected void closeSpeakerOn() {
        try {
            if(vol > 0){
                audioManager.setStreamVolume(voiceType, vol, 0);
            }
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable upload = new Runnable() {
        @Override
        public void run() {
            if(new File(picFile).exists()){
                tt.showMessage("拍照成功，正在保存……", tt.SHORT);
                presenter.uploadImg();
            } else if(count > 0){
                handler.postDelayed(upload, 100);
                count--;
            } else {
                tt.showMessage("拍照失败", tt.SHORT);
            }
        }
    };

    @Override
    public File getPhoto() {
        return new File(picFile);
    }

    /**
     * 播放拨号响铃
     */
    protected void playMakeCallSounds() {
        try {
            audioManager.setMode(AudioManager.MODE_RINGTONE);

            // 播放
            streamID = soundPool.play(outgoing, // 声音资源
                    7f, // 左声道
                    7f, // 右声道
                    1, // 优先级，0最低
                    -1, // 循环次数，0是不循环，-1是永远循环
                    1); // 回放速度，0.5-2.0之间。1为正常速度
        } catch (Exception e) {
            ILog.e(e);
        }
    }

    public void sendAction(int action){
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
            cmdMsg.addBody(new EMCmdMessageBody("{\"data\":\"" + showAction[action] + "\", \"type\":4}"));
            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
            showMessage("已经发送给" + robot.getName() + "，请稍等");
        }
    }

    public void sendText(){
        if(StringHandler.isEmpty(etSend.getText().toString())){
            showMsg("系统提示", "要发送的内容不能为空");
        }
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
            cmdMsg.addBody(new EMCmdMessageBody("{\"data\":\"" + etSend.getText().toString() + "\", \"type\":1}"));
            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
            etSend.setText("");
            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        shutDown();
        super.onBackPressed();
    }

    @Override
    public void showMessage(String msg) {
        tt.showMessage("" + msg, tt.SHORT);
    }

    @Override
    protected void onDestroy() {
        shutDown();
        closeSpeakerOn();
        if (soundPool != null)
            soundPool.release();
        handler.removeCallbacks(upload);
        handler.removeCallbacks(hide);
        handler.removeCallbacks(startLoading);
        handler.removeCallbacks(link);
        wakeLock.release();
        super.onDestroy();
    }
}
