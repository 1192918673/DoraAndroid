package coms.geeknewbee.doraemon.box.smart_home.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import cn.com.broadlink.blnetwork.BLNetwork;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.SharedPreferencesTool;

/**
 * 【记录】<br/>
 * 1. 进入遥控器控制之前，首先调用isInitSuccess，查看是否已经初始化，如果没有初始化，则先初始化；<br/>
 * 2. 初始化完成后，调用isSetWifi，检测是否已经设置WIFI，如果已经设置WIFI，跳转到4，否则跳转到3<br/>
 * 3. 调用broadLinkEasyconfig方法，设置WIFI<br/>
 * 4. 调用broadLinkProbeList，获得设备列表，找到博联设备，调用broadLinkDeviceAdd连接上，并记录MAC地址<br/>
 * 5. 调用broadLinkRMProScanAndStudy学习遥控器，调用broadLinkRMProCode，获得学习到的数据<br/>
 * 6. 调用broadLinkRMProSend，使用学习到的遥控器指令<br/>
 * Created by Administrator on 2016/6/23.
 */
public class BLM {

    public static final int MSG_WHAT_BL_INIT = 1;

    public static final int MSG_WHAT_BL_PROBELIST = 11;

    public static final int MSG_WHAT_BL_DEVICEADD = 12;

    public static final int MSG_WHAT_BL_AUTH = 41;

    public static final int MSG_WHAT_BL_PLUG = 72;

    public static final int MSG_WHAT_BL_SCANSTUDY = 132;

    public static final int MSG_WHAT_BL_CODE = 133;

    public static final int MSG_WHAT_BL_SEND = 134;

    public static final int MSG_WHAT_BL_EASYCONFIG = 10000;

    public static final String BL = "10002";

    public static final String SP = "10024";

    private static final String BROADLINK_USER_LICENSE = "aiotYtzPTYvo0c1rKoyS8zXkWr/yKN794" +
            "M4SVRZVeMLbA8E2qFczFkycKEfgNBzd+djigmirvq2LpcLbVA7rhydxZwqKchdcUQzPWM4OqJES0e3uls8=";

    private static final String BROADLINK_TYPE_LICENSE = "hJ2tiIpCJavZZMaZe+yWEYx7Fi3Euw07m4" +
            "+vxSi/3aGItD+NviHtOKoOgbm/PYkbN+i36skA/Emzn32DSPvDY4FDbPAGyN7IIh1xTZpgrUogDtG9m" +
            "XJsA1Htm+2xqw7CfaRZ2iZbtdGtM76e0yF4Rg==";

    private static final int DELAY = 1000;

    private static BLNetwork blNetwork;

    private static boolean is_bl_init_success = false;

    private static boolean blIsSetWifi = false;

    private static boolean spIsSetWifi = false;

    private static BLLocalProbeListResponse blDeviceList;

    private static boolean broadLinkDeviceReady;

    private static BLLocalDevice blDevice;

    private static String blMac;

    private static String spMac;

    private static String code;

    private static boolean stop = false;

    private static Handler handler;

    private static SharedPreferencesTool spt;

    private static String device;

    /**
     * 是否已经完成初始化
     * @return
     */
    public static boolean isInitSuccess(){
        return is_bl_init_success;
    }

    public static String getMacAddress(){
        if(blMac == null)
            blMac = spt.getString("blMac", null);
        return blMac;
    }

    /**
     * 是否已经设置WIFI
     * @return
     */
    public static boolean isSetWifi(SharedPreferencesTool spt){
        if(!blIsSetWifi){
            blIsSetWifi = spt.getBoolean("blIsSetWifi", false);
        }
        if(blMac == null)
            blMac = spt.getString("blMac", null);
        BLM.spt = spt;
        return blIsSetWifi;
    }

    public static String getSpMacAddress(){
        if(spMac == null)
            spMac = spt.getString("spMac", null);
        return spMac;
    }

    /**
     * 是否已经设置WIFI
     * @return
     */
    public static boolean isSetSpWifi(SharedPreferencesTool spt){
        if(!spIsSetWifi){
            spIsSetWifi = spt.getBoolean("blIsSetSpWifi", false);
        }
        if(spMac == null)
            spMac = spt.getString("spMac", null);
        BLM.spt = spt;
        return spIsSetWifi;
    }

    public static void cancel(){
        stop = true;
    }

    /**
     * 初始化BroadLink
     */
    public static void broadLinkInit(Handler handler, Context context) {
        stop = false;
        if(blNetwork == null)
            blNetwork = BLNetwork.getInstanceBLNetwork(context);
        BLM.handler = handler;
        spt = new SharedPreferencesTool(context);
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(1);
        blLocalCMD.setCommand("network_init");
        blLocalCMD.put("license", BROADLINK_USER_LICENSE);
        blLocalCMD.put("type_license", BROADLINK_TYPE_LICENSE);
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_INIT, DELAY, 0);
    }

    /**
     * 配置BroadLink的网络
     */
    public static void broadLinkEasyconfig(Handler handler, String ssid, String pwd, String gate, String device) {
        stop = false;
        BLM.device = device;
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(10000);
        blLocalCMD.setCommand("easyconfig");
        blLocalCMD.put("ssid", ssid);
        blLocalCMD.put("password", pwd);
        blLocalCMD.put("dst", gate);
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_EASYCONFIG, DELAY, 0);
    }

    /**
     * 获取所有设备列表
     */
    public static void broadLinkProbeList(Handler handler) {
        stop = false;
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(11);
        blLocalCMD.setCommand("probe_list");
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_PROBELIST, DELAY, 0);
    }

    /**
     * 设置插座状态
     * @param handler
     */
    public static void authPlugbase(Handler handler) {
        stop = false;
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(MSG_WHAT_BL_AUTH);
        blLocalCMD.setCommand("sp1_auth");
        blLocalCMD.put("mac", blMac);
        blLocalCMD.put("password", "5119959");
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_AUTH, DELAY, 0);
    }

    /**
     * 设置插座状态
     * @param handler
     * @param status 0，关闭，1，打开
     */
    public static void modifyPlugbase(Handler handler, int status) {
        stop = false;
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(72);
        blLocalCMD.setCommand("sp2_control");
        blLocalCMD.put("mac", "b4:43:0d:cb:a0:db");
        blLocalCMD.put("status", status);
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_PLUG, DELAY, 0);
    }

    /**
     * 对设备进行初始化
     * "mac":"00:11:22:33:44:55",
     * "type":"RM2",
     * "name":"智能插座",
     * "lock":0,
     * "password":1028000492,
     * "id":0,
     * "subdevice":0, "key":"00000000000000000000000000000000"
     */
    public static void broadLinkDeviceAdd(Handler handler, BLLocalDevice device) {
        stop = false;
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(12);
        blLocalCMD.setCommand("device_add");
        blLocalCMD.put("mac", blMac);
        blLocalCMD.put("type", device.getMac());
        blLocalCMD.put("name", device.getName());
        blLocalCMD.put("lock", device.getLock());
        blLocalCMD.put("password", device.getPassword());
        blLocalCMD.put("id", device.getId());
        blLocalCMD.put("subdevice", device.getSubdevice());
        blLocalCMD.put("key", device.getKey());
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_DEVICEADD, DELAY, 0);
    }

    /**
     * 启动扫频学习
     */
    public static void broadLinkRMProScanAndStudy(Handler handler) {
        stop = false;
        ILog.e("----------------------学习遥控指令----------------------");
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(132);
        blLocalCMD.setCommand("rm2_study");
        blLocalCMD.put("mac", blMac);
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_SCANSTUDY, DELAY, 0);
    }

    /**
     * 获取学习到的代码
     */
    public static void broadLinkRMProCode(Handler handler) {
        stop = false;
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(133);
        blLocalCMD.setCommand("rm2_code");
        blLocalCMD.put("mac", blMac);
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_CODE, DELAY, 0);
    }

    /**
     * 发送命令代码到设备
     */
    public static void broadLinkRMProSend(Handler handler, String cmd_data) {
        BLM.handler = handler;
        BLLocalCMD blLocalCMD = new BLLocalCMD();
        blLocalCMD.setApi_id(134);
        blLocalCMD.setCommand("rm2_send");
        blLocalCMD.put("mac", blMac);
        blLocalCMD.put("data", cmd_data);
        sendCMDToBroadLink(blLocalCMD.getCMDString(), MSG_WHAT_BL_SEND, DELAY, 0);
    }

    /**
     * 发送命令给BroadLink的设备
     *
     * @param cmd
     */
    private static void sendCMDToBroadLink(final String cmd, final int msg_what, final int delay, final int bls1_what) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ILog.e("BroadLink发送命令：" + cmd);
                String blResponse = blNetwork.requestDispatch(cmd);
                BLLocalResponse response = new Gson().fromJson(blResponse, BLLocalResponse.class);
                switch (msg_what) {
                    case MSG_WHAT_BL_INIT:
                        if (response.getCode() == 0 || response.getCode() == 3) {
                            is_bl_init_success = true;
                            if (response.getCode() == 3) {
                                blIsSetWifi = true;
                                spt.putBoolean("blIsSetWifi", true);
                            }
                        } else {
                            ILog.e("BroadLink初始化失败：" + response.getMsg());
                            if(!stop)
                                broadLinkInit(handler, null);
                            else
                                stop = false;
                        }
                        break;
                    case MSG_WHAT_BL_EASYCONFIG:
                        if (response.getCode() == 0) {
                            blIsSetWifi = true;
                            spt.putBoolean("blIsSetWifi", true);
                        }
                        break;
                    case MSG_WHAT_BL_PROBELIST:
                        if (response.getCode() == 0) {
                            blDeviceList = new Gson().fromJson(blResponse, BLLocalProbeListResponse.class);
                            if (blDeviceList == null || blDeviceList.getList() == null || blDeviceList.getList().size() == 0) {
                                broadLinkProbeList(handler);
                            } else {
                                for (BLLocalDevice localDevice :
                                        blDeviceList.getList()) {
                                    if (localDevice.getType().equals(BL) && BL.equals(device)) {
                                        ILog.e("-----------"+localDevice.getType()+"-"+localDevice.getName()+"----------");
                                        blDevice = localDevice;
                                        blMac = blDevice.getMac();
                                        spt.putString("blMac", blMac);
                                        BLM.broadLinkDeviceAdd(handler,localDevice);
                                    } else if(localDevice.getType().equals(SP) && SP.equals(device)){
                                        ILog.e("-----------"+localDevice.getType()+"-"+localDevice.getName()+"----------");
                                        blDevice = localDevice;
                                        spMac = blDevice.getMac();
                                        spt.putString("spMac", spMac);
                                        BLM.broadLinkDeviceAdd(handler,localDevice);
                                    } else {
                                        ILog.e("------------------------------------");
                                        ILog.e("------------------------------------");
                                        ILog.e("-----------"+localDevice.getType()+"-"+localDevice.getName()+"----------");
                                        ILog.e("------------------------------------");
                                        ILog.e("------------------------------------");
                                    }
                                }
                            }
                        } else {
                            blDeviceList = null;
                            ILog.e("BroadLink 获取设备列表失败：" + response.getMsg());
                            if(!stop)
                                broadLinkProbeList(handler);
                            else
                                stop = false;
                        }
                        break;
                    case MSG_WHAT_BL_SCANSTUDY:
                        if (response.getCode() == 0) {
                            ILog.e("BroadLink SCANSTUDY返回成功：" + response.getMsg());
                            broadLinkRMProCode(handler);
                        } else {
                            ILog.e("BroadLink SCANSTUDY失败：" + response.getMsg());
                            if(!stop)
                                broadLinkRMProScanAndStudy(handler);
                            else
                                stop = false;
                        }
                        break;
                    case MSG_WHAT_BL_CODE:
                        if (response.getCode() == 0) {
                            BLLocalDeviceCode cmdData = new Gson().fromJson(blResponse, BLLocalDeviceCode.class);
                            ILog.e("学习指令成功 : " + cmdData.getData().length() + " - " + cmdData.getData());
                            code = cmdData.getData();
                        } else {
                            ILog.e("BroadLink SCANSTUDY失败：" + response.getMsg());
                            if(!stop)
                                broadLinkRMProCode(handler);
                            else
                                stop = false;
                        }
                        break;
                    case MSG_WHAT_BL_DEVICEADD:
                        if (response.getCode() == 0) {
                            ILog.e("BroadLink DEVICEADD返回成功：" + response.getMsg());
                            ILog.e("BroadLink设备准备就绪");
                            broadLinkDeviceReady = true;
                        } else {
                            ILog.e("BroadLink DEVICEADD失败：" + response.getMsg());
                        }
                        break;
                    case MSG_WHAT_BL_AUTH:
                        if (response.getCode() == 0) {
                            modifyPlugbase(handler, 0);
                        }
                        break;
                    case MSG_WHAT_BL_PLUG:
                        if (response.getCode() == 0) {

                        }
                        break;
                }
                ILog.e("BroadLink返回：" + blResponse);
                Message msg = new Message();
                msg.what = msg_what;
                msg.obj = blResponse;
                msg.arg1 = bls1_what;
                handler.sendMessage(msg);
            }
        };
        thread.start();
    }
}
