package com.mining.app.zxing.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;

/**
 * Created by Administrator on 2016/6/1.
 */

public interface ViewListener {

    /**
     * 获得二维码扫描组件
     * @return
     */
    public ViewfinderView getViewfinderView();

    /**
     * 处理扫描结果
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode);

    /**
     * 设置返回的参数
     * @param result
     * @param data
     */
    public void setResult(int result, Intent data);

    /**
     * 关闭界面
     */
    public void finish();

    /**
     * 启动新界面
     * @param intent
     */
    public void startActivity(Intent intent);

    /**
     * 重绘
     */
    public void drawViewfinder();

    /**
     * 获得Handler
     * @return
     */
    public Handler getHandler();
}
