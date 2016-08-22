package coms.geeknewbee.doraemon.index.center;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.center.presenter.SuggestionPresenter;
import coms.geeknewbee.doraemon.index.center.view.ISuggestionView;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;

/**
 * Created by chen on 2016/4/7
 */
public class SuggestionsActivity extends BaseActivity implements ISuggestionView {


    private ImageButton ibBack;
    private TextView tvTitle;
    private EditText etSuggestions;
    private TextView tvRest;
    private Button btConfirm;

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        etSuggestions = (EditText) findViewById(R.id.et_suggestions);
        tvRest = (TextView) findViewById(R.id.tv_rest);
        btConfirm = (Button) findViewById(R.id.bt_confirm);
    }

    private int num = 300;

    SuggestionPresenter presenter = new SuggestionPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        assignViews();
        initListener();
    }

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close_left, 0);
    }

    private void initListener() {
        //返回
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestionsActivity.this, CenterActivity.class));
                SuggestionsActivity.this.finish();
            }
        });
        //剩余字数监听
        etSuggestions.addTextChangedListener(textWatcher);
        //确定
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendSuggestion();
                clear();
            }
        });
    }

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
            if(number > 0){
                tvRest.setText("还可以输入" + number + "字");
            } else {
                tvRest.setText(Html.fromHtml("还可以输入<font color='#f00'>" + number + "</font>字"));
            }
        }
    };


    @Override
    public String goBack() {
        return null;
    }

    @Override
    public String getSuggestion() {
        return etSuggestions.getText().toString().trim();
    }

    @Override
    public void showLoading() {
        showDialog("正在提交您的反馈信息……");
    }

    @Override
    public void hideLoading() {
        hideDialog();
        finish();
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN,null);
    }

    @Override
    public void clear() {
        etSuggestions.setText("");
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }

    @Override
    public void showMessage(String msg) {
        showMsg("系统提示", msg);
    }
}
