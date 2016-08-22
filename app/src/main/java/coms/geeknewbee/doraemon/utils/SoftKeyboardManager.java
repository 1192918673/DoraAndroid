package coms.geeknewbee.doraemon.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SoftKeyboardManager {
	
	InputMethodManager imm;
	
	EditText editText;
	
	public SoftKeyboardManager(EditText editText){
		if(editText != null){
			imm =(InputMethodManager) editText.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		this.editText = editText;
	}
	
	public void hide(){
		if(imm != null && editText != null){
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}

	public void show(){
		if(imm != null && editText != null){
			imm.showSoftInput(editText, 0);
		}
	}
}
