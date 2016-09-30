package coms.geeknewbee.doraemon.readface.bean;

/**
 * Created by GYY on 2016/9/29.
 */
public class ReadFaceInitParams {
    public int orientation;
    public int resizeScale;
    public int iw;
    public int ih;

    public ReadFaceInitParams(int orientation, int resizeScale, int iw, int ih) {
        this.orientation = orientation;
        this.resizeScale = resizeScale;
        this.iw = iw;
        this.ih = ih;
    }
}
