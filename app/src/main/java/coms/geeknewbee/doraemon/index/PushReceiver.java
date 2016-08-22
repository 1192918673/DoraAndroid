package coms.geeknewbee.doraemon.index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.igexin.sdk.PushConsts;

import org.json.JSONObject;

import java.util.List;

import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;

public class PushReceiver extends BroadcastReceiver {

	public static final String ACTION_BATTERY_CHANGED = "OnBatteryChanged";
	
	Session session = Session.getSession();
	
	/**
	 * 收到了消息
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle bundle = intent.getExtras();
		ILog.e("onReceive() action=" + bundle.getInt(PushConsts.CMD_ACTION));
		
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
        case PushConsts.GET_MSG_DATA:
            // 获取透传（payload）数据
            byte[] payload = bundle.getByteArray("payload");
            if (payload != null) {
                String content = new String(payload);
                ILog.e("message st : " + content);
                
                try {
        			JSONObject json = new JSONObject(content);
        			
        			int type = json.getInt("type");

        			switch (type) {
        			case 2:
        				// TODO 电量产生变化
						String robotPk = json.getJSONObject("data").getString("robot_pk");
						int percent = json.getJSONObject("data").getInt("percent");
						if(session.contains(ACTION_BATTERY_CHANGED)){
							OnBatteryChanged onBatteryChanged = (OnBatteryChanged)session.get(ACTION_BATTERY_CHANGED);
							if(onBatteryChanged == null){
								onBatteryChanged(robotPk, percent, true);
							} else {
								onBatteryChanged(robotPk, percent, false);
								onBatteryChanged.onBatteryChanged(percent);
							}
						} else {
							onBatteryChanged(robotPk, percent, true);
						}
        			default:
        				break;
        			}
        		} catch(Exception exception){
        			exception.printStackTrace();
        		}
            }
            break;
                
         default:
         break;
    	}
	}

	private void onBatteryChanged(String robotPk, int percent, boolean refresh){
		if(robotPk == null){
			return;
		}
		if(session.contains("robotBeans")){
			List<RobotBean> robotBeans =
					(List<RobotBean>)session.get("robotBeans");
			if(robotBeans.size() > 0){
				int len = robotBeans.size();
				for(int i = 0; i < len; i++){
					if(robotPk.equals("" + robotBeans.get(i).getId())){
						robotBeans.get(i).setBattery(percent);
						if(refresh)
							session.remove("index_refresh");
						break;
					}
				}
			}
		}
	}

	public static interface OnBatteryChanged{
		public void onBatteryChanged(int percent);
	}
}
