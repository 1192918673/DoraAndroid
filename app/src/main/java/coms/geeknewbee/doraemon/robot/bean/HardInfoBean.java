package coms.geeknewbee.doraemon.robot.bean;

/**
 * Created by Administrator on 2016/6/1.
 * 机器人
 */

public class HardInfoBean {

    private String battery;

    private String ram;

    private String serial_no;

    private String cpu;

    private String rom;

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    @Override
    public String toString() {
        return battery + " - " + ram + " - " + serial_no + " - " + cpu + " - " + rom;
    }
}
