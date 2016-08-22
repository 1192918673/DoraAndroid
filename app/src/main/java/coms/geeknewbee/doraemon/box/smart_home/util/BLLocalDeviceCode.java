package coms.geeknewbee.doraemon.box.smart_home.util;

/**
 * Created by echo on 16/3/22.
 */
public class BLLocalDeviceCode extends BLLocalResponse {

    /**
     * data : 2600e6006f390e2a0e290d100d100e0f0e290e0f0e0f0e2a0e290d100e2a0e0e0f0e0e2a0d2a0e0f0e2a0e
     */

    private String mac;

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    private String data;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
