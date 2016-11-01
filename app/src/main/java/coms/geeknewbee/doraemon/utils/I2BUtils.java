package coms.geeknewbee.doraemon.utils;

/**
 * Created by GYY on 2016/11/1.
 */
public class I2BUtils {
    public static byte[] int2bytes(int num) {
        byte[] b = new byte[4];
        int mask = 0xff;
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) ((num >>> (24 - i * 8)) & mask);
        }
        return b;
    }
}
