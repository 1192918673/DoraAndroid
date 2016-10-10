package coms.geeknewbee.doraemon.robot.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.IMergeRule;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AICloudTTSEngine;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.engines.AILocalTTSEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.aispeech.export.listeners.AITTSListener;
import com.aispeech.speech.AIAuthEngine;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import coms.geeknewbee.doraemon.robot.RobotVoiceActivity;
import coms.geeknewbee.doraemon.utils.Session;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 思必驰语音处理类
 * Created by kevin on 16-6-6.
 */
public class AISpeech {
    private final AIASRListener aiasrListener;
    private RobotVoiceActivity activity;

    public static final String APPKEY = "147601158385960f";// 测试激活码用的appkey
    public static final String SECRETKEY = "1898fa1e09c7fdb3dfd059b35fa22489";//添加您的SECRETKEY"
    AIAuthEngine mAuthEngine;
    public boolean isAuthority = false;
    private boolean isAuthrizing = false;
    private AIMixASREngine mASREngine;

    static final String zipFileName = "tts.zip";
    AILocalGrammarEngine mGrammarEngine;
    public AICloudTTSEngine mTTSEngine;
    private AILocalTTSEngine mLocalTTSEngine;

    public AISpeech(RobotVoiceActivity activity, AIASRListener aiasrListener) {
        this.activity = activity;
        this.aiasrListener = aiasrListener;
        mAuthEngine = AIAuthEngine.getInstance(this.activity);
        try {
            mAuthEngine.init(APPKEY, SECRETKEY, "");
            if (!this.activity.getAiSpeechIsAuthority()) {
                LogUtils.e("AISpeech未申请授权，发起申请");
                authority();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }// TODO 换成您的s/n码
        initLocalTTSEngine();
        initGrammarEngine();
        // 检测是否已生成并存在识别资源，若已存在，则立即初始化本地识别引擎，否则等待编译生成资源文件后加载本地识别引擎
        if (new File(Util.getResourceDir(this.activity) + File.separator + AILocalGrammarEngine.OUTPUT_NAME)
                .exists()) {
            initAsrEngine();
        } else {
            startResGen();
        }
    }

    /**
     * 获取授权
     */
    public void authority() {
        isAuthrizing = true;
        Observable
                .create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        boolean authRet = mAuthEngine.doAuth();
                        subscriber.onNext(authRet);
                        subscriber.onCompleted();
                        AISpeech.this.activity.setAiSpeechIsAuthority(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        isAuthority = b;
                        isAuthrizing = false;
//                        initCloudASR();
                    }
                });

    }

    /**
     * 初始化资源编译引擎
     */
    private void initGrammarEngine() {
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
        }
        LogUtils.e("grammar create");
        mGrammarEngine = AILocalGrammarEngine.createInstance();
        mGrammarEngine.setResFileName(SampleConstants.ebnfc_res);
        mGrammarEngine
                .init(this.activity, new AILocalGrammarListenerImpl(), APPKEY, SECRETKEY);
        mGrammarEngine.setDeviceId((String) Session.getSession().get(Session.DEVICE_ID));
    }

    /**
     * 初始化云端语音识别引擎
     */
    public void initCloudASR() {
        if (mASREngine == null) {
            mASREngine = AIMixASREngine.createInstance();
            mASREngine.setVadResource(SampleConstants.vad_res);
            mASREngine.setDeviceId((String) Session.getSession().get(Session.DEVICE_ID));
            mASREngine.init(this.activity, new AICloudASRListenerImpl(), APPKEY, SECRETKEY);
            mASREngine.setNBest(1);
            mASREngine.setNoSpeechTimeOut(0);
            mASREngine.setMaxSpeechTimeS(30);
//            mASREngine.setAttachAudioUrl(true);
//            mASREngine.setUseSex(true);
            //smEngine.setUseCnConf(true);
//        mASREngine.start();
        }
        try {
            mASREngine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.e("initCloudASR完成...");
    }

    /**
     * 初始化本地语音合成引擎
     */
    private void initLocalTTSEngine() {
        if (mLocalTTSEngine != null) {
            mLocalTTSEngine.destory();
        }
        mLocalTTSEngine = AILocalTTSEngine.createInstance();
        mLocalTTSEngine.setResource(zipFileName, "zhilingf.v0.4.11.bin");
        // mLocalTTSEngine.setModelPath(Environment.getExternalStorageDirectory() +
        // "/ttsRes/" + modelName);
        // 开启本地合成缓存,缓存音频条数为20条,缓存文件在 外存->Android->data->包名->cache->ttsCache目录下
        mLocalTTSEngine.setRealBack(true);
        mLocalTTSEngine.setUseCahce(false, 20);
        mLocalTTSEngine.init(this.activity, new AILocalTTSListenerImpl(), APPKEY, SECRETKEY);
        mLocalTTSEngine.setLeftMargin(125);
        mLocalTTSEngine.setRightMargin(25);
        mLocalTTSEngine.setSpeechRate(0.9f);
        mLocalTTSEngine.setDeviceId((String) Session.getSession().get(Session.DEVICE_ID));
        mLocalTTSEngine.setSavePath(Environment.getExternalStorageDirectory() + "/linzhilin/localtts.wav");

        LogUtils.e("initLocalTTSEngine完成...");
    }

    /**
     * 初始化本地合成引擎
     */
    public void initAsrEngine() {
        if (mASREngine != null) {
            mASREngine.destroy();
        }
        LogUtils.e("asr create");
        mASREngine = AIMixASREngine.createInstance();
        mASREngine.setResBin(SampleConstants.ebnfr_res);
        mASREngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME, true);

        mASREngine.setVadResource(SampleConstants.vad_res);
        if (this.activity.getExternalCacheDir() != null) {
            mASREngine.setTmpDir(this.activity.getExternalCacheDir().getAbsolutePath());
            mASREngine.setUploadEnable(true);
            mASREngine.setUploadInterval(1000);
        }

        mASREngine.setMaxSpeechTimeS(30);
        mASREngine.setServer("ws://s.api.aispeech.com");
        mASREngine.setRes("robot");
        mASREngine.setUseXbnfRec(true);
        mASREngine.setUsePinyin(true);
        mASREngine.setUseForceout(false);
        mASREngine.setAthThreshold(0.4f);//设置本地置信度阀值
        mASREngine.setIsRelyOnLocalConf(true);//是否开启依据本地置信度优先输出,如需添加例外
        mASREngine.setLocalBetterDomains(new String[]{"aihomeopen", "aihomegoods", "aihomeplay", "aihomenum",
                "aihomenextup", "aihomehello"});//设置本地擅长的领域范围
        mASREngine.setWaitCloudTimeout(2000);
        mASREngine.setPauseTime(1000);
        mASREngine.setUseConf(true);
        mASREngine.setNoSpeechTimeOut(5000);
        mASREngine.setDeviceId((String) Session.getSession().get(Session.DEVICE_ID));
        // 自行设置合并规则:
        // 1. 如果无云端结果,则直接返回本地结果
        // 2. 如果有云端结果,当本地结果置信度大于阈值时,返回本地结果,否则返回云端结果
        mASREngine.setMergeRule(new IMergeRule() {

            @Override
            public AIResult mergeResult(AIResult localResult, AIResult cloudResult) {

                AIResult result = null;
                try {
                    if (cloudResult == null) {
                        // 为结果增加标记,以标示来源于云端还是本地
                        JSONObject localJsonObject = new JSONObject(localResult.getResultObject()
                                .toString());
                        localJsonObject.put("src", "native");

                        localResult.setResultObject(localJsonObject);
                        result = localResult;
                    } else {
                        JSONObject cloudJsonObject = new JSONObject(cloudResult.getResultObject()
                                .toString());
                        cloudJsonObject.put("src", "cloud");
                        cloudResult.setResultObject(cloudJsonObject);
                        result = cloudResult;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;

            }
        });
        mASREngine.init(this.activity, this.aiasrListener, APPKEY, SECRETKEY);
        mASREngine.setUseCloud(true);//该方法必须在init之后

        LogUtils.e("asr engine init 成功");
        mASREngine.start();
    }

    public void setASRListener(AIASRListener listener) {
        mASREngine.setListener(listener);
    }

    private class AILocalTTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            LogUtils.e("初始化完成，返回值：" + status);
            LogUtils.e("onInit");
            if (status == AIConstant.OPT_SUCCESS) {
                LogUtils.e("初始化成功!");
            } else {
                LogUtils.e("初始化失败!code:" + status);
            }
        }

        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
        }

        @Override
        public void onError(String utteranceId, AIError error) {
            LogUtils.e("检测到错误");
            LogUtils.e("Error:" + error.toString());
            if (error.getErrId() == 70724) {//Auth failed
                LogUtils.e("AISpeech发起授权申请！");
                authority();
            }
        }

        @Override
        public void onReady(String utteranceId) {
            LogUtils.e("开始播放");
        }

        @Override
        public void onCompletion(String utteranceId) {
            LogUtils.e("合成完成");
        }
    }

    public void ASRStart() {
        mASREngine.start();
    }

    public void ASRStop() {
        mASREngine.stopRecording();
        mASREngine.destroy();
    }

    /**
     * 开始生成识别资源
     */

    private void startResGen() {
        // 生成ebnf语法
        GrammarHelper gh = new GrammarHelper(this.activity);
        String contactString = gh.getConatcts();
        String appString = gh.getApps();
        // 如果手机通讯录没有联系人
        if (TextUtils.isEmpty(contactString)) {
            contactString = "无联系人";
        }
        String ebnf = gh.importAssets(contactString, appString, "grammar.xbnf");
        // 设置ebnf语法
        mGrammarEngine.setEbnf(ebnf);
        // 启动语法编译引擎，更新资源
        mGrammarEngine.update();
    }

    /**
     * 语法编译引擎回调接口，用以接收相关事件
     */
    public class AILocalGrammarListenerImpl implements AILocalGrammarListener {

        @Override
        public void onError(AIError error) {
            LogUtils.e("资源生成发生错误");
            LogUtils.e(error.getError());
        }

        @Override
        public void onUpdateCompleted(String recordId, String path) {
            LogUtils.e("资源生成/更新成功\npath=" + path + "\n重新加载识别引擎...");
            initAsrEngine();
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                LogUtils.e("资源定制引擎加载成功");
            } else {
                LogUtils.e("资源定制引擎加载失败");
            }
        }
    }

    /**
     * 云端语音识别默认监听实现
     */
    private class AICloudASRListenerImpl implements AIASRListener {

        @Override
        public void onReadyForSpeech() {
            LogUtils.e("请说话...");
        }

        @Override
        public void onBeginningOfSpeech() {
            LogUtils.e("检测到说话");
        }

        @Override
        public void onEndOfSpeech() {
            LogUtils.e("检测到语音停止，开始识别...\n");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            LogUtils.e("RmsDB = " + rmsdB);
        }

        @Override
        public void onError(AIError error) {
            LogUtils.e("AICloudASRListenerImpl error:" + error.toString());
        }

        @Override
        public void onResults(AIResult results) {
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                    LogUtils.e("result JSON = " + results.getResultObject().toString());
                    // 可以使用JSONResultParser来解析识别结果
                    // 结果按概率由大到小排序
                    JSONResultParser parser = new JSONResultParser(results.getResultObject()
                            .toString());
                    String[] recs = parser.getNBestRec();
                    for (int i = 0; i < recs.length; i++) {
                        LogUtils.e("rec:" + recs[i] + "\n");
                    }
                }
            }
        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                LogUtils.e("初始化成功!");
            } else {
                LogUtils.e("初始化失败!code:" + status);
            }
        }

        @Override
        public void onRecorderReleased() {
            LogUtils.e("检测到录音机停止\n");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }
    }

    public void initTTSEngine(AITTSListener listener) {
        // 创建云端合成播放器
        mTTSEngine = AICloudTTSEngine.createInstance();
        mTTSEngine.init(this.activity, listener, APPKEY, SECRETKEY);
        // 指定默认中文合成
        mTTSEngine.setLanguage(AIConstant.CN_TTS);

        // 默认女声
        mTTSEngine.setRes("syn_chnsnt_zhilingf");
        mTTSEngine.setDeviceId((String) Session.getSession().get(Session.DEVICE_ID));

        mTTSEngine.setSavePath("/sdcard/aispeech_doraemon.mp3");
    }

    public void speak(String text) {
        if (NetworkStateReceiver.isConnected) {
            mTTSEngine.speak(text, "1024");
        } else {
            mLocalTTSEngine.speak(text, "1024");
        }
    }


    public void onStop() {
        if (mASREngine != null) {
            mASREngine.cancel();
        }
        if (mTTSEngine != null) {
            mTTSEngine.stop();
        }
    }

    public void onDestroy() {
        if (mASREngine != null) {
            mASREngine.destroy();
            mASREngine = null;
        }
        if (mTTSEngine != null) {
            mTTSEngine.destory();
        }
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
            mGrammarEngine = null;
        }
    }
}
