package coms.geeknewbee.doraemon.robot.readface;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.Gson;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.communicate.socket.SocketManager;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.robot.readface.bean.ReadFaceInitParams;
import mobile.ReadFace.YMFaceTrack;

public class ReadFaceActivity extends BaseActivity implements View.OnClickListener {

    private SocketManager socketManager;

    /**
     * -----------------------组件----------------------
     **/
    private ImageButton ibBack;
    private Button addFace;
    private Button addFaceSelf;
    private Button deleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_face);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        addFace = (Button) findViewById(R.id.addFace);
        addFaceSelf = (Button) findViewById(R.id.addFaceSelf);
        deleteAll = (Button) findViewById(R.id.deleteAll);

        ibBack.setOnClickListener(this);
        addFace.setOnClickListener(this);
        addFaceSelf.setOnClickListener(this);
        deleteAll.setOnClickListener(this);

        socketManager = SocketManager.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.addFace:
                Intent addIntent = new Intent(ReadFaceActivity.this, InsertFaceActivity.class);
                startActivity(addIntent);
                break;
            case R.id.addFaceSelf:
                Intent addSelfIntent = new Intent(ReadFaceActivity.this, AddFaceActivity.class);
                startActivity(addSelfIntent);
                break;
            case R.id.deleteAll:
                ReadFaceInitParams initParams = new ReadFaceInitParams(YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_1920, 0, 0);
                Gson gson = new Gson();
                String json = gson.toJson(initParams);
                String data = GlobalContants.DELETE_ALL_FACE + json;
                socketManager.writeInfo(data.getBytes(), 2);
                break;
        }
    }
}
