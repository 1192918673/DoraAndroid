package coms.geeknewbee.doraemon.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import coms.geeknewbee.doraemon.db.DbTalkTemplateDao;
import coms.geeknewbee.doraemon.entity.ItemTalkTemplate;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;

/**
 * Created by chen on 2016/3/16
 * 学说话-增加 页面
 */
public class AddTalkActivity extends Activity implements View.OnClickListener {

    private TextView tv_Rest;
    private ImageButton ib_Back;
    private TextView tv_Insert;
    private EditText et_Que;
    private EditText et_Ans;
    private DbTalkTemplateDao mDao;
    private ItemTalkTemplate itt;
    private int num = 300;

    /**
     * TextWatcher:接口。继承它要实现其三个方法，分别在EditText改变前，改变过程中，改变之后各自发生的动作
     */
    TextWatcher textWatcher = new TextWatcher() {
        private CharSequence temp;
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //将每次输入都记录
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            //获得当前输入的字符数
            int number = num - s.length();
            //动态显示剩余字数
            tv_Rest.setText("还可以输入" + number + "字");
            selectionStart = et_Ans.getSelectionStart();
            selectionEnd = et_Ans.getSelectionEnd();
            if (temp.length() > num) {
                s.delete(selectionStart - 1, selectionEnd);
                int tempSelection = selectionStart;
                et_Ans.setText(s);
                et_Ans.setSelection(tempSelection);//将光标设置最后
                tv_Rest.setTextColor(Color.RED);
            } else {
                tv_Rest.setTextColor(Color.parseColor("#9FA0A0"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_talk);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ib_Back = (ImageButton) findViewById(R.id.ib_back);
        tv_Insert = (TextView) findViewById(R.id.tv_insert);
        et_Que = (EditText) findViewById(R.id.et_que);
        et_Ans = (EditText) findViewById(R.id.et_ans);
        tv_Rest = (TextView) findViewById(R.id.tv_rest);
        ib_Back.setOnClickListener(this);
        tv_Insert.setOnClickListener(this);
        //剩余字数监听
        et_Ans.addTextChangedListener(textWatcher);
    }

    /**
     * 控件点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                //返回到学说话界面
                startActivity(new Intent(AddTalkActivity.this, LearnTalkActivity.class));
                finish();
                break;
            case R.id.tv_insert:
                //返回到学说话界面 -> 不返回 改成返回。返回到学说话界面。重新加载数据
                insertTemplateData();
                et_Que.requestFocus();
                break;
            default:
                break;
        }
    }

    /**
     * 将对话模板保存到本地 并保存到服务器
     */
    private void insertTemplateData() {
        String que = et_Que.getText().toString().trim();
        String ans = et_Ans.getText().toString().trim();
        if (TextUtils.isEmpty(que) || TextUtils.isEmpty(ans)) {
            Toast.makeText(this, "输入和输出不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mDao = new DbTalkTemplateDao(getApplicationContext());
        boolean result = mDao.insert(que, ans);
        if (result) {
            itt = new ItemTalkTemplate();
            itt.que = que;
            itt.ans = ans;
            Toast.makeText(getApplicationContext(), "数据库添加成功", Toast.LENGTH_SHORT).show();
            //并保存到服务器
            uploadTemplate(itt);
        } else {
            Toast.makeText(getApplicationContext(), "数据库添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加成功后保存到服务器
     */
    private void uploadTemplate(ItemTalkTemplate itt) {
        //设置请求参数
        RequestParams params = new RequestParams();

        params.addBodyParameter("question", itt.que);
        params.addBodyParameter("answer", itt.ans);

        String token = SharePreferenceUtils.getString(getApplicationContext(), "token", "null");
        HttpUtils http = new HttpUtils();
        String url = GlobalContants.CUSTOM_ANSWERS+"?token="+token;

        http.send(HttpRequest.HttpMethod.POST,
                url,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Toast.makeText(getApplicationContext(), "服务器保存成功", Toast.LENGTH_SHORT).show();
                        et_Que.setText("");
                        et_Ans.setText("");
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(getApplicationContext(), error.getExceptionCode() + ":" + msg, Toast.LENGTH_SHORT).show();

                    }
                });

    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }
}
