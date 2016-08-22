package coms.geeknewbee.doraemon.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.entity.ConvertText;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;

/**
 * Created by chen on 2016/3/16
 * 对话页面
 */
public class ConversationActivity extends Activity implements View.OnClickListener {

    private EditText et_Txt;
    private Button btn_Send;
    private ImageButton ib_Back;
    private TextView tv_Rest;//剩余输入字数

    private int num = 30;
    /**
     * TextWatcher:接口。继承它要实现其三个方法，分别在EditText改变前，改变过程中，改变之后各自发生的动作
     */
    TextWatcher textWatcher = new TextWatcher() {
        private CharSequence temp;
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //charSequence = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //将每次输入都记录
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            //获取已输入长度
            int number = num - s.length();
            tv_Rest.setText("还可以输入" + number + "字");
            selectionStart = et_Txt.getSelectionStart();
            selectionEnd = et_Txt.getSelectionEnd();
            if (temp.length() > num) {
                s.delete(selectionStart - 1, selectionEnd);
                int tempSelection = selectionStart;
                et_Txt.setText(s);
                et_Txt.setSelection(tempSelection);//将光标设置最后
                tv_Rest.setTextColor(Color.RED);
            } else {
                tv_Rest.setTextColor(Color.parseColor("#9FA0A0"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initView();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    /**
     * 初始化View
     */
    private void initView() {
        tv_Rest = (TextView) findViewById(R.id.tv_rest);
        et_Txt = (EditText) findViewById(R.id.et_txt);
        btn_Send = (Button) findViewById(R.id.btn_send);
        ib_Back = (ImageButton) findViewById(R.id.ib_goback);
        ib_Back.setOnClickListener(this);
        btn_Send.setOnClickListener(this);
        //剩余字数监听
        et_Txt.addTextChangedListener(textWatcher);
    }

    /**
     * 点击发送，要语音输出的文本发送给服务器
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                //获取文本
                String txt = et_Txt.getText().toString().trim();
                if(!TextUtils.isEmpty(txt)){
                    ConvertText convertText = new ConvertText();
                    convertText.text = txt;
                    upload(convertText);
                }else{
                    Toast.makeText(getApplicationContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
                    et_Txt.setText("");
                    et_Txt.requestFocus();
                }
                break;
            case R.id.ib_goback:
                //返回首页
                startActivity(new Intent(ConversationActivity.this, IndexActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 上传
     */
    private void upload(ConvertText ct) {
        //设置请求参数
        RequestParams params = new RequestParams();
        params.addBodyParameter("text", ct.text);

        String token = SharePreferenceUtils.getString(getApplication(), "token", "null");

        HttpUtils http = new HttpUtils();
        String url = GlobalContants.SEND_VOICE+"?token="+token;

        http.send(HttpRequest.HttpMethod.POST,
                url,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        et_Txt.setText("");
                    }
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(getApplicationContext(), error.getExceptionCode() + ":" + msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
