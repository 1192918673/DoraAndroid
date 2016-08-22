package coms.geeknewbee.doraemon.box.smart_home.util;

/**
 * Created by echo on 16/3/22.
 */
public class BLLocalDevice {

    /**
     * mac : 00:11:22:33:44:55
     * type : SP2
     * name : 智能插座
     * lock : 0
     * password : 987961777
     * id : 0
     * subdevice : 0
     * key : 097628343fe99e23765c1513accf8b02
     */

    private String mac;
    private String type;
    private String name;
    private int lock;
    private int password;
    private int id;
    private int subdevice;
    private String key;

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSubdevice(int subdevice) {
        this.subdevice = subdevice;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMac() {
        return mac;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getLock() {
        return lock;
    }

    public int getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public int getSubdevice() {
        return subdevice;
    }

    public String getKey() {
        return key;
    }
}
