package coms.geeknewbee.doraemon.box.custom_answers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.custom_answers.presenter.CusAnswersAddPresenter;
import coms.geeknewbee.doraemon.box.custom_answers.view.ICusAnswerAddView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;

/**
 * Created by chen on 2016/3/16
 * 学说话-增加 页面
 */
public class AddTalkActivity extends BaseActivity implements ICusAnswerAddView {

    private ImageButton ib_Back;
    private TextView tv_Insert;
    private EditText et_Que;
    private EditText et_Ans;
    private TextView tv_Rest;

    String robotPk;

    private void assignViews() {
        ib_Back = (ImageButton) findViewById(R.id.ib_back);
        tv_Insert = (TextView) findViewById(R.id.tv_insert);
        et_Que = (EditText) findViewById(R.id.et_que);
        et_Ans = (EditText) findViewById(R.id.et_ans);
        tv_Rest = (TextView) findViewById(R.id.tv_rest);

        robotPk = getIntent().getStringExtra("robotPk");
        skm = new SoftKeyboardManager(et_Que);
    }

    private int num = 30;

    CusAnswersAddPresenter presenter = new CusAnswersAddPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_talk);
        assignViews();
        initListener();
    }


    /**
     * 初始化控件
     */
    private void initListener() {
        //返回
        ib_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();
            }
        });
        //添加
        tv_Insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("正在添加学说话记录……");
                skm.hide();
                presenter.create();
            }
        });
        //剩余字数监听
        et_Ans.addTextChangedListener(textWatcher);
        et_Ans.setOnFocusChangeListener(focusChangeListener);
        et_Que.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };


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

    //设置关闭动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    @Override
    public String getQuestion() {
        return et_Que.getText().toString().trim();
    }

    @Override
    public String getAnswer() {
        return et_Ans.getText().toString().trim();
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN,null);
    }

    @Override
    public String getRobot_PK() {
        return robotPk;
    }

    @Override
    public void showSuccess() {
        hideDialog();
        et_Que.setText("");
        et_Ans.setText("");
        showMsg("系统提示", "添加成功！您可以继续编辑添加，也可以返回查看已添加的记录。");
        session.put("ca_refresh", true);
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", "" + msg);
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }
}
