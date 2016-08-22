package coms.geeknewbee.doraemon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.hyphenate.media.EMOppositeSurfaceView;

import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by Administrator on 2016/7/4.
 */
public class EMView extends EMOppositeSurfaceView {

    public EMView(Context context, AttributeSet attr){
        super(context, attr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90, 0, 0);
        canvas.translate((getWidth() - getHeight()) / 2, getWidth());
        super.dispatchDraw(canvas);
        canvas.restore();
        ILog.e("-----------------------222----------------------");
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90, 0, 0);
        canvas.translate((getWidth() - getHeight()) / 2, getWidth());
        super.draw(canvas);
        canvas.restore();
        ILog.e("-----------------------111----------------------");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ILog.e("-----------------------333----------------------");
    }
}
