package coms.geeknewbee.doraemon.global;

import com.igexin.sdk.PushManager;

import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * Created by chen on 2016/3/18
 */
public class GlobalContants {

    //蓝牙名
    public static final String ROBOT_BT_NAME = "Doraemon";

    public static final String APP_URL = "http://doraemon.babadeyan.com:80/app";//app前缀
    public static final String SEND_SUGGESTION = APP_URL+"/feedback";

    //------------------------------------------------------------------------------------------

    public static final String AUTH_URL = "http://doraemon.babadeyan.com:80/auth/";//auth前缀
    public static final String GET_CODE = AUTH_URL + "/sms";
    public static final String GET_TOKEN = AUTH_URL + "/token";
    public static final String CREATE_PWD = AUTH_URL + "/password";
    public static final String GET_USER = AUTH_URL + "/user";

    //------------------------------------------------------------------------------------------


    public static final String ROBOTS_URL = "http://doraemon.babadeyan.com:80/robots/";//robots前缀
    public static final String CUSTOM_ANSWERS = ROBOTS_URL;//自定义问答
    //http://doraemon.microfastup.com:80/robots/3/alarms?token=adfc494d0a716dc1d784b6f4a19fd7a3b67d2de2&page=1
    public static final String GET_ALARMS = ROBOTS_URL;//提醒
    //http://doraemon.microfastup.com:80/robots/3/photos?token=034b7e9e0b27213b6525c1357448cd54038cf9eb&page=1
    public static final String GET_PHOTOS = ROBOTS_URL;//照片
    //http://doraemon.microfastup.com:80/robots/3/study_records?token=034b7e9e0b27213b6525c1357448cd54038cf9eb&page=1
    public static final String GET_RECORDS = ROBOTS_URL;//学英语
    //------------------------------------------------------------------------------------------


    //http://doraemon.microfastup.com:80/robots/1/voice
    public static final String TALKING_URL = "http://doraemon.babadeyan.com:80/robots/";//talking前缀

    public static final String SEND_VOICE = TALKING_URL;//对话地址

    //------------------------------------------------------------------------------------------


    //robots下:
    //储物http://doraemon.microfastup.com:80/robots/1/goods?token=fe027ef528067c64b8cef32bf49a1327bd5df3cd
    public static final String STORE_ROOM = ROBOTS_URL;

    //------------------------------------------------------------------------------------------
    //事件通知
    //http://doraemon.microfastup.com:80/robots/5/events?token=42683f1067c317f0669d25b0774ad337b6480c1e&page=1
    public static final String GET_EVENTS = ROBOTS_URL;

    //个推APPID
    //public static final String APP_ID = "jUbgDpdDhm9YvYCrRyln9";

    //个推ClientId
    public static final String CLIENT_ID = PushManager.getInstance().getClientid(MyApplication.getContext());

    //public static final String CLIENT_ID = "005c2a6c8618512a0160eb70a7411411";


    public static final String ROBOT_PK = "10";


}
