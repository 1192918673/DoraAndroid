package coms.geeknewbee.doraemon.robot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.bean.HardInfoBean;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotHardInfoPresenter;
import coms.geeknewbee.doraemon.robot.presenter.IRobotManagerPresenter;
import coms.geeknewbee.doraemon.robot.view.IHardInfoView;
import coms.geeknewbee.doraemon.robot.view.IManagerView;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class RobotHardInfoActivity extends BaseActivity implements IHardInfoView {

    /**-----------------------组件----------------------**/

    TextView dr_cpu;

    TextView dr_ram;

    TextView dr_rom;

    TextView dr_battery;

    TextView dr_id;

    ImageButton ib_back;

    /**-----------------------数据----------------------**/

    IRobotHardInfoPresenter presenter;

    String robotKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_hardinfo);
        assignViews();
    }

    private void assignViews() {
        dr_cpu = (TextView) findViewById(R.id.dr_cpu);
        dr_ram = (TextView) findViewById(R.id.dr_ram);
        dr_rom = (TextView) findViewById(R.id.dr_rom);
        dr_battery = (TextView) findViewById(R.id.dr_battery);
        dr_id = (TextView) findViewById(R.id.dr_id);
        ib_back = (ImageButton) findViewById(R.id.ib_back);

        presenter = new IRobotHardInfoPresenter(this);
        ib_back.setOnClickListener(clickListener);

        robotKey = getIntent().getStringExtra("robotKey");

        showDialog("正在读取硬件信息……");
        presenter.getHardInfo();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.ib_back:
                    finish();
                    break;
            }
        }
    };

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public String getRobotPk() {
        return robotKey;
    }

    @Override
    public void setHardInfo(HardInfoBean hardInfo) {
        hideDialog();
        dr_cpu.setText(hardInfo.getCpu());
        dr_ram.setText(hardInfo.getRam());
        dr_rom.setText(hardInfo.getRom());
        dr_battery.setText(hardInfo.getBattery());
        dr_id.setText(hardInfo.getSerial_no());
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        showMsg("系统提示", msg);
    }
}
