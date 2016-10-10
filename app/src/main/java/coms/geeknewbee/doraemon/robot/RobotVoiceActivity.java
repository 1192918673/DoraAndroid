package coms.geeknewbee.doraemon.robot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AITTSListener;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Callable;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.bean.UserBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotManagerPresenter;
import coms.geeknewbee.doraemon.robot.utils.AISpeech;
import coms.geeknewbee.doraemon.robot.utils.NetworkStateReceiver;
import coms.geeknewbee.doraemon.robot.view.IManagerView;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class RobotVoiceActivity extends BaseActivity {

    /**-----------------------组件----------------------**/

    TextView tvVolume;

    TextView tvVoice;

    TextView tvResult;

    ImageButton ibBack;

    Button btnSpeek;

    /**-----------------------语音识别数据----------------------**/

    public boolean getAiSpeechIsAuthority() {
        return aiSpeechIsAuthority;
    }

    public void setAiSpeechIsAuthority(boolean aiSpeechIsAuthority) {
        this.aiSpeechIsAuthority = aiSpeechIsAuthority;
        spt.putBoolean("aiSpeechIsAuthority", aiSpeechIsAuthority);
    }

    private boolean aiSpeechIsAuthority;

    AISpeech aiSpeech;

    Handler handler = new Handler();

    boolean stop = false;

    String voice = "=========================";

    /**-----------------------数据----------------------**/

    String robotPk;

    UserBean user;

    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_voice);
        assignViews();

        robotPk = getIntent().getStringExtra("robotPk");
    }

    private void assignViews() {
        tvVolume = (TextView) findViewById(R.id.tvVolume);
        tvVoice = (TextView) findViewById(R.id.tvVoice);
        tvResult = (TextView) findViewById(R.id.tvResult);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        btnSpeek = (Button) findViewById(R.id.btnSpeek);

        aiSpeechIsAuthority = spt.getBoolean("aiSpeechIsAuthority", false);

        ibBack.setOnClickListener(clickListener);
        btnSpeek.setOnClickListener(clickListener);

        ILog.e("-------------------"+session.contains(Session.USER)+"-------------------");
        if(!session.contains("EMClient.login") && session.contains(Session.USER)){
            user = (UserBean) session.get(Session.USER);
            login();
        } else {
            handler.postDelayed(_close, 200);
        }

        aiSpeech = new AISpeech(this, new AICloudASRListenerImpl());
        //startListenTo();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.ibBack:
                    finish();
                    break;

                case R.id.btnSpeek:
                    startListenTo();
                    tvVoice.setText("正在启动录音服务...");
                    btnSpeek.setText("正在启动录音服务...");
                    btnSpeek.setBackgroundColor(0xff666666);
                    btnSpeek.setOnClickListener(null);
                    break;
            }
        }
    };

    public void login(){
        if(!session.contains("EMClient.login")){
            EMClient.getInstance().login(user.getHx_user().getUsername(),
                    user.getHx_user().getPassword(), new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    session.put("EMClient.login", true);
                                    handler.postDelayed(_close, 200);
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

    /**
     * 云端语音识别监听实现
     */
    private class AICloudASRListenerImpl implements AIASRListener {

        @Override
        public void onReadyForSpeech() {
            ILog.e("请说话...");
            tvVoice.setText("准备就绪，请说话...");
            btnSpeek.setText("准备就绪，请说话...");
            tvResult.setText("");
        }

        @Override
        public void onBeginningOfSpeech() {
            ILog.e("检测到说话");
            tvVoice.setText("正在说话...");
            btnSpeek.setText("正在说话...");
            tvResult.setText("");
        }

        @Override
        public void onEndOfSpeech() {
            tvVoice.setText("开始识别...");
            tvResult.setText("");
            ILog.e("开始识别...");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            int n = Math.round(rmsdB / 4);
            if(n >= voice.length()){
                tvVolume.setText("=" + voice);
            } else {
                tvVolume.setText("=" + voice.substring(0, n));
            }
        }

        @Override
        public void onError(final AIError error) {
            LogUtils.e("AICloudASRListenerImpl error:" + error.toString());

            new Thread() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (error.getErrId() != 70905 && error.getErrId() != 70904) {
                                tvVoice.setText("未能识别");
                                tvResult.setText("");
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            //startListenTo();
                            btnSpeek.setText("点击说话");
                            btnSpeek.setBackgroundColor(0xFF00A0E8);
                            btnSpeek.setOnClickListener(clickListener);
                        }
                    });


                }
            }.start();
        }

        @Override
        public void onResults(AIResult results) {
            LogUtils.e("result JSON = " + results.getResultObject().toString());
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                    // 可以使用JSONResultParser来解析识别结果
                    // 结果按概率由大到小排序
                    JSONResultParser parser = new JSONResultParser(results.getResultObject()
                            .toString());
                    String[] recs = parser.getNBestRec();

                    for (int i = 0; i < recs.length; i++) {
                        LogUtils.e("rec:" + recs[i] + "\n");
                    }
                    JSONObject result = parser.getResultJSON();
                    String output = result.optString("output");
                    String input = result.optString("input");
                    if (input.length() == 0 && recs.length > 0) {
                        input = recs[0];
                    }
                    LogUtils.e("分析结果：" + input + "," + output);
//                    startRead(output, emptyAction);

                    if (output.indexOf("为您搜索:") == 0) {
                        output = "";
                    }
                    if (input.length() > 1) {
                        tvVoice.setText("" + input);
                        tvResult.setText("" + output);
                        sendVolume(input + "｜" + output,
                                results.getResultObject().toString());
                    }
                }
                //startListenTo();
                btnSpeek.setText("点击说话");
                btnSpeek.setBackgroundColor(0xFF00A0E8);
                btnSpeek.setOnClickListener(clickListener);
            }
        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                LogUtils.e("初始化成功!");
            } else {
                tvVoice.setText("初始化失败!code:" + status);
            }
        }

        @Override
        public void onRecorderReleased() {
//            LogUtils.e("检测到录音机停止\n");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }
    }

    private void startListenTo() {
        // mResultText.setText(null);// 清空显示内容
//        if (!NetworkStateReceiver.isConnected) {
//            LogUtils.e("启动语音监听失败，没有网络");
//            handler.postDelayed(restart, 3000);
//            return;
//        }
        try {
            if(!stop)
                aiSpeech.ASRStart();
        } catch (Exception e) {
            LogUtils.i("startListenTo：" + e.getLocalizedMessage());
        }
    }

    Runnable _close = new Runnable() {
        @Override
        public void run() {
            ILog.e("sendVolume : _close");
            sendVolume("_close", "{}");
        }
    };

    Runnable restart = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startListenTo();
                }
            });
        }
    };

    public void sendVolume(String data, String json){
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
            data = data.trim().replaceAll("~", "到").replaceAll("℃", "度")
                    .replaceAll("≤", "不大于").replaceAll("≥", "不小于").replaceAll(" ", "");

            cmdMsg.addBody(new EMCmdMessageBody("{\"data\":\"" + data
                    + "\", \"json\":" + json + ", \"type\":5}"));

            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
        }
    }

    @Override
    protected void onDestroy() {
        sendVolume("_open", "{}");
        wakeLock.release();
        stop = true;
        aiSpeech.ASRStop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }
}
