package coms.geeknewbee.doraemon.box.alarm_clock;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.alarm_clock.presenter.AlarmAddPresenter;
import coms.geeknewbee.doraemon.box.alarm_clock.view.IAlarmsAddView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;


/**
 * Created by chen on 2016/4/7
 */
public class AddAlarmsActivity extends BaseActivity implements IAlarmsAddView {

    private ImageButton ibBack;
    private LinearLayout llCenter;
    private EditText etContent;
    //repeat=1:重复->每天一次(rbEvery.isChecked=true);repeat=0:仅一次(rbOnce.isChecked=true)
    private RadioButton rbOnce;
    private RadioButton rbEvery;
    private RelativeLayout rlContent;
    private TextView tvContent;
    private Button confirm;
    private Calendar calendar;
    private ProgressBar progressBar;

    //保存两个字段的值alarm_date&alarm_time
    private String strDate = new String();
    private String strTime = new String();
    private String dateTxt = "";
    private String timeTxt = "";

    private boolean flag = false;

    String robotPK;

    AlarmAddPresenter presenter = new AlarmAddPresenter(this);

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        llCenter = (LinearLayout) findViewById(R.id.ll_center);
        etContent = (EditText) findViewById(R.id.et_content);
        rbOnce = (RadioButton) findViewById(R.id.rb_once);
        rbEvery = (RadioButton) findViewById(R.id.rb_every);
        rlContent = (RelativeLayout) findViewById(R.id.rl_content);
        tvContent = (TextView) findViewById(R.id.tv_content);
        confirm = (Button) findViewById(R.id.confirm);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        skm = new SoftKeyboardManager(etContent);
        robotPK = getIntent().getStringExtra("robotPk");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clock);
        assignViews();
        initListener();
    }

    private void initListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //时间选择器
        rlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置""
                tvContent.setText("");
                strDate = "";
                strTime = "";
                calendar = Calendar.getInstance();
                if (rbOnce.isChecked()) {
                    flag = false;
                    //选择仅一次时
                    datePicker();
                    timePicker();
                }
                if (rbEvery.isChecked()) {
                    flag =true;
                    timePicker();
                }
            }
        });
        //确定添加提醒
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("正在提交闹钟提醒信息……");
                presenter.create();
            }
        });
        etContent.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };

    //弹出日期选择器
    private void datePicker() {
        //日期选择
        new DatePickerDialog(AddAlarmsActivity.this,
                // 绑定监听器
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        strDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        dateTxt = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日  ";
                        //保存选中的年月日: 2015-12-1
                        tvContent.setText(dateTxt + timeTxt);
                    }
                }
                // 设置初始日期
                , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH)).show();
    }

    //弹出时间选择器
    private void timePicker() {
        //直接创建一个TimePickerDialog对话框实例，并显示出来
        //时间选择
        new TimePickerDialog(AddAlarmsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //String date = tvContent.getText().toString().trim();
                strTime = hourOfDay + ":" + minute;
                timeTxt = "  " + hourOfDay + "时" + minute + "分";
                if(!flag){
                    tvContent.setText(dateTxt + timeTxt);
                }else {
                    tvContent.setText(timeTxt);
                }

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(AddAlarmsActivity.this)).show();
    }

    @Override
    public String getContent() {
        return etContent.getText().toString().trim();
    }

    @Override
    public String getAlarm_Date() {
        if (rbEvery.isChecked()) {
            return null;
        } else {
            return strDate;
        }
    }

    @Override
    public String getAlarm_Time() {
        return strTime;
    }

    @Override
    public int getRepeat() {
        if (rbEvery.isChecked()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void addSuccess() {
        hideDialog();
        etContent.setText("");
        tvContent.setText("");
        session.put("al_refresh", true);
        showMessage("添加成功！您可以继续编辑添加，也可以返回查看已添加的记录。");
    }

    @Override
    public String getRobot_PK() {
        return robotPK;
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
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
