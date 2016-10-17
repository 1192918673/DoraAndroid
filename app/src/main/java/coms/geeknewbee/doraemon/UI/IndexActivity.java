package coms.geeknewbee.doraemon.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.utils.ToastTool;

/**
 * 首页
 */
public class IndexActivity extends BaseActivity implements View.OnClickListener {

    private Button bt_Menu;
    private ToastTool tt;
    private RelativeLayout mRl;

    @Override
    public void onBackPressed() {
        if (tt.isDoubleClick("再按一次退出应用")) {
            session.closeAllActivities();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
    }

    private void initView() {
        bt_Menu = (Button) findViewById(R.id.bt_menu);
        mRl = (RelativeLayout) findViewById(R.id.rl);
        bt_Menu.setOnClickListener(this);
        tt = ToastTool.getToast(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_menu:
                //TODO 跳转百宝箱
                startActivity(new Intent(IndexActivity.this, BoxActivity.class));
                this.overridePendingTransition(R.anim.activity_open, 0);
                break;
            default:
                break;
        }
    }
}
