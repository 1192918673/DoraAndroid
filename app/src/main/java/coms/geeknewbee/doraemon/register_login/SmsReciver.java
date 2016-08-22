package coms.geeknewbee.doraemon.register_login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Administrator on 2016/5/30.
 * 接收短信
 */
public class SmsReciver extends BroadcastReceiver {

    private static OnSmsRecived onSmsRecived;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(onSmsRecived == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                if (msg.getMessageBody() != null
                        && msg.getMessageBody().contains("三个爸爸机器人")) {
                    // 截取验证码
                    String message = msg.getMessageBody();
                    if(message.contains("验证码是") && message.contains("，")){
                        String code = message.substring(message.indexOf("验证码是") + 4,
                                message.indexOf("，"));
                        if(code != null && code.length() > 0){
                            onSmsRecived.sendCode(code);
                        }
                    }
                }

            }
        }
    }

    public static void setOnSmsRecived(OnSmsRecived onSmsRecived) {
        SmsReciver.onSmsRecived = onSmsRecived;
    }

    public interface OnSmsRecived{
        public void sendCode(String code);
    }
}