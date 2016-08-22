package coms.geeknewbee.doraemon.box.storeroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.storeroom.presenter.AddGoodsPresenter;
import coms.geeknewbee.doraemon.box.storeroom.view.IAddGoodsView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.utils.SharePreferenceUtils;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;

/**
 * Created by lenovo on 2016/4/21.
 */
public class AddGoodsActivity extends BaseActivity implements IAddGoodsView {

    private ImageButton ibBack;
    private EditText etGoods;
    private EditText etPlace;
    private Button btConfirm;

    String robotPk;

    private int num = 30;

    private void initView(){
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        etGoods = (EditText) findViewById(R.id.et_goods);
        etPlace  = (EditText) findViewById(R.id.et_place);
        btConfirm = (Button) findViewById(R.id.confirm);

        skm = new SoftKeyboardManager(etGoods);
        robotPk = getIntent().getStringExtra("robotPk");
    }

    AddGoodsPresenter presenter = new AddGoodsPresenter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        initView();
        initListener();
    }

    private void initListener() {
        //返回
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确定
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skm.hide();
                showDialog("正在保存储物记录……");
                presenter.create();
            }
        });
        etGoods.setOnFocusChangeListener(focusChangeListener);
        etPlace.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };


    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN,null);
    }

    @Override
    public String getName() {
        return etGoods.getText().toString().trim();
    }

    @Override
    public String getPlace() {
        return etPlace.getText().toString().trim();
    }

    @Override
    public String getRobot_PK() {
        return robotPk;
    }

    @Override
    public void showSuccess() {
        hideDialog();
        etGoods.setText("");
        etPlace.setText("");
        showMsg("系统提示", "添加成功！您可以继续编辑添加，也可以返回查看已添加的记录。");
        session.put("good_refresh", true);
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
