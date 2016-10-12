package coms.geeknewbee.doraemon.global;

import com.igexin.sdk.PushManager;

import coms.geeknewbee.doraemon.R;

/**
 * Created by chen on 2016/3/18
 */
public class GlobalContants {

    //蓝牙名
//    public static final String ROBOT_BT_NAME = "DoraemonTestLIX";   //DoraemonLiXiang  //DoraemonTest    //DoraemonTestLX

    //发送蓝牙命令的前后缀
    public static final String COMMAND_ROBOT_PREFIX = new String(new byte[]{0x02});
    public static final String COMMAND_ROBOT_SUFFIX = new String(new byte[]{0x03});

    //socket传输数据的头尾标志
    public static final String COMMAND_ROBOT_PREFIX_FOR_SOCKET = "ROBOT_PREFIX";
    public static final String COMMAND_ROBOT_SUFFIX_FOR_SOCKET = "ROBOT_SUFFIX";


    //发送数据的功能码
    public static final int SEND_SOCKET_CONTROL = 1;
    public static final int READY_ADD_FACE = 2;
    public static final int ADD_FACE_DATA = 3;
    public static final int NAME_DATA = 4;
    public static final int ADD_FACE_PHOTO = 5;
    public static final int DELETE_ALL_FACE = 6;

    //socket端口号
    public static final int PORT = 9000;

    //        "move", "forward", "backward", "left", "right", "disconnect", "intro_self", head_left", "head_right", "head_up",
//     "head_down", "l_arm_front","l_arm_end", "l_arm_up", "l_arm_down", "r_arm_front", "r_arm_end","r_arm_up", "r_arm_down", "head_front",
// "say_hi", "end_say", "can_do", "how_teach", "for_old", "hh"
    //控制所用
    public static String[] img = {null, null, null, null, null, null, "wei_xiao", null, null, null,
            null, null, null, null, null, null, null, null, null, null,
            "wei_xiao", "wei_xiao", "de_yi", "wei_xiao", "wei_xiao", "ke_lian_meng"};
    public static String[] word = {null, null, null, null, null, null, "我叫哆啦a梦，来自22世纪。如果你们以为我只有颜值那可就错啦，我上知天文，下懂地理，数学计算，语文国学，唱歌跳舞，这些都难不倒我。", null, null, null,
            null, null, null, null, null, null, null, null, null, null,
            "大家好", "谢谢大家，期待越来越多的朋友帮我变得更聪明", "我可以在幼儿园教小朋友们学习英语，明年我还要去台湾的老人院帮忙照看老人，听说还有很多品牌邀请我去商场超市做推广呐", "我可以辅助老师，完成专业教育机构的英语课程，我可是有专业级的英语发音呢。", "我可以带老人家做早操，提醒老人吃药，还可以给大家读报，和家人互动。", "这是我第一次来呼和浩特，这里风景优美，人民能歌善舞，真希望可以在这里常住哦。"};
    public static int[] act = {-1, R.raw.action_foot_forward, R.raw.action_foot_backward, R.raw.action_foot_left, R.raw.action_foot_right, -1, R.raw.action_arm_up_down_move_self, R.raw.action_head_left, R.raw.action_head_right, R.raw.action_head_up,
            R.raw.action_head_down, R.raw.action_l_arm_front, R.raw.action_l_arm_end, R.raw.action_l_arm_up, R.raw.action_l_arm_down, R.raw.action_r_arm_front, R.raw.action_r_arm_end, R.raw.action_r_arm_up, R.raw.action_r_arm_down, R.raw.action_head_front,
            R.raw.action_arm_up_down_move, R.raw.action_arm_up_down_move, R.raw.action_head_up_down, R.raw.action_arm_up_down_move_self, R.raw.action_take_exercises, R.raw.action_head_hug};

    // 表情gif名称
    public static String[] emotion = {"wei_xiao", "dai", "wei_xiao", "ji_dong", "yi_wen", "yun", "se", "dai", "hai_xiu", "ku",
            "gan_ga", "jing_kong", "de_yi", "bai_yan", "ke_lian_meng", "nu", "jing_kong", "ji_yan", "wei_xiao", "wei_xiao"};
    public static String[] sound = {"太对了", "宝宝不同意", "好好开心啊", "好激动啊", "宝宝不知道", "晕死了", "么么哒", "我想静静", "我都不好意思了", "好难过",
            "我竟无言以对", "当时我就惊呆了", "宝宝聪明不", "宝宝鄙视你", "感觉自己萌萌哒", "气死宝宝了", "好怕怕啊", "再见，来不及挥手", "快来抱抱", "欢迎欢迎，热烈欢迎"};
    // 动作RawID
    public static int[] action = {R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi,
            R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi, R.raw.tong_yi};

    //------------------------------------------------------------------------------------------

    public static final String APP_URL = "http://doraemon.babadeyan.com:80/app";//app前缀
    public static final String SEND_SUGGESTION = APP_URL + "/feedback";

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
