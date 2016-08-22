package coms.geeknewbee.doraemon.box.smart_home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;

public class SmartTVActivity extends BaseActivity {

    ImageButton ibBack;

    LinearLayout llBuy;

    LinearLayout llSet;

    Button btNext;

    String robotPk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_tv);

        initViews();
    }

    public void initViews(){

        ibBack = (ImageButton)findViewById(R.id.ibBack);
        llBuy = (LinearLayout)findViewById(R.id.llBuy);
        llSet = (LinearLayout)findViewById(R.id.llSet);
        btNext = (Button)findViewById(R.id.btNext);

        robotPk = getIntent().getStringExtra("robotPk");

        ibBack.setOnClickListener(clickListener);
        btNext.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ibBack:
                    finish();
                    break;

                case R.id.btNext:
                    Intent intentTv = new Intent(SmartTVActivity.this, SmartTVWifiActivity.class);
                    intentTv.putExtra("robotPk", robotPk);
                    startActivity(intentTv);
                    finish();
                    break;
            }
        }
    };
}
