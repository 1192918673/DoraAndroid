package coms.geeknewbee.doraemon.global;

import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;

import coms.geeknewbee.doraemon.register_login.Splash2Activity;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.SharedPreferencesTool;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.ToastTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 基础界面
 */
public class BaseActivity extends Activity {
	
	public ToastTool tt;
	
	protected SharedPreferencesTool spt;
	
	protected SoftKeyboardManager skm;

	protected Session session = Session.getSession();
	
	ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tt = ToastTool.getToast(this);
		spt = new SharedPreferencesTool(this);
		if(!session.contains(session.DEVICE_ID)){
			session.initialize(this);
			String Clientid = PushManager.getInstance().getClientid(this);
			if(Clientid == null){
				Clientid = spt.getString(Session.CLIENT_ID, null);
			} else {
				spt.putString(Session.CLIENT_ID, Clientid);
			}
			session.put(Session.CLIENT_ID, Clientid);
		}
		session.restore(savedInstanceState);
		session.addActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		session.save(outState);
	}
	
	@Override
	protected void onDestroy() {
		Session.removeActivity(this.getClass().getSimpleName());
		super.onDestroy();
	}
	
	public void showDialog(String message){
		if(pd == null){
			pd = new ProgressDialog(this);
			pd.setMessage(message);
			pd.setCancelable(false);
			pd.show();
		} else {
			pd.setMessage(message);
			pd.show();
		}
	}
	
	public void hideDialog(){
		if(pd != null){
			pd.dismiss();
			pd = null;
		}
	}

	public void loginTimeout(){
		session.closeAllActivities();
		Intent intent = new Intent(getApplication(), Splash2Activity.class);
		startActivity(intent);
		tt.showMessage("登录超时，请重新登录", tt.SHORT);
	}

	public boolean hasDialog(){
		return pd != null && pd.isShowing();
	}

	public void showMsg(String title, String msg){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this)
				.setTitle("" + title).setMessage("" + msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	public void asyncToast(String msg){
		Message m = new Message();
		m.what = 0;
		m.obj = msg;
		__h.sendMessage(m);
	}

	public void asyncMsg(String msg){
		Message m = new Message();
		m.what = 1;
		m.obj = msg;
		__h.sendMessage(m);
	}

	Handler __h = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String m = (String)msg.obj;
			if(msg.what == 0){
				tt.showMessage("" + m, tt.SHORT);
			} else {
				showMsg("系统提示", "" + m);
			}
			super.handleMessage(msg);
		}
	};
}
