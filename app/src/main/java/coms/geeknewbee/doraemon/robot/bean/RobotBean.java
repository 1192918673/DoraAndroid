package coms.geeknewbee.doraemon.robot.bean;

/**
 * Created by Administrator on 2016/6/1.
 * 机器人
 */

public class RobotBean {

    private int id;

    private String version;

    private String status;

    private String name;

    private String date_created;

    private String role;

    private String hx_username;

    private int volume;

    private String ssid;

    private int battery;

    private String serial_no;

    public int getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getRole() {
        return role;
    }

    public String getHx_username() {
        return hx_username;
    }

    public int getVolume() {
        return volume;
    }

    public String getSsid() {
        return ssid;
    }

    public int getBattery() {
        return battery;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setHx_username(String hx_username) {
        this.hx_username = hx_username;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    @Override
    public String toString() {
        return id + " - " + version + " - " + status + " - " + name
                + " - " + date_created + " - " + role + " - " + hx_username
                + " - " + volume + " - " + ssid + " - " + battery + " - " + serial_no;
    }
}
