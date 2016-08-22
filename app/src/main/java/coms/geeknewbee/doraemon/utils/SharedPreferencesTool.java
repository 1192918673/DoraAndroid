package coms.geeknewbee.doraemon.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 本地设置的存储类，用于处理SharedPreferences中的数据
 * get方法是从中取值
 * put方法是赋值
 * @author Li Shaoqing
 *
 */
public class SharedPreferencesTool {
	
	private SharedPreferences sp;
	
	public SharedPreferencesTool(Context context){
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public boolean getBoolean(String key, boolean value){
		return sp.getBoolean(key, value);
	}
	
	public int getInt(String key, int value){
		return sp.getInt(key, value);
	}
	
	public long getLong(String key, long value){
		return sp.getLong(key, value);
	}
	
	public String getString(String key, String value){
		return sp.getString(key, value);
	}
	
	public float getFloat(String key, float value){
		return sp.getFloat(key, value);
	}
	
	public void putBoolean(String key, boolean value){
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public void putInt(String key, int value){
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void putLong(String key, long value){
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public void putString(String key, String value){
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public void putFloat(String key, float value){
		Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
}