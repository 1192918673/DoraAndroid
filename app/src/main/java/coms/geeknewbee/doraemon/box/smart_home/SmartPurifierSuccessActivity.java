package coms.geeknewbee.doraemon.box.smart_home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;

public class SmartPurifierSuccessActivity extends BaseActivity {

    ImageButton ibBack;

    TextView tvTitle;

    TextView tvContent;

    Button btnRelink;

    String robotPk;

    /**
     * 0，净化器连接成功，1，净化器已连接，2，智能插座已连接
     */
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_purifier_success);
        assignViews();
    }

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvContent = (TextView)findViewById(R.id.tvContent);
        btnRelink = (Button)findViewById(R.id.btnRelink);

        type = getIntent().getIntExtra("type", 0);
        robotPk = getIntent().getStringExtra("robotPk");

        if(type == 1){
            tvTitle.setText("净化器已连接");
            btnRelink.setVisibility(View.VISIBLE);
        } else if(type == 2){
            tvTitle.setText("智能插座已连接");
            btnRelink.setVisibility(View.VISIBLE);
            tvContent.setText("现在您可以对机器人说：\n“打开电源”\n“关闭电源”");
            btnRelink.setText("重新配网连接");
        }

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnRelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == 1){
                    Intent intent = new Intent(SmartPurifierSuccessActivity.this,
                            SmartPurifierCodeActivity.class);
                    intent.putExtra("robotPk", robotPk);
                    startActivity(intent);
                    finish();
                } else if(type == 2){
                    Intent intent = new Intent(SmartPurifierSuccessActivity.this,
                            SmartPlugActivity.class);
                    intent.putExtra("robotPk", robotPk);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
