package coms.geeknewbee.doraemon.box.storeroom.bean;

import java.util.List;

/**
 * Created by chen on 2016/4/11
 */
public class StoreRoomBean {
    public int id;
    public String name;
    public String place;
    public String date_created;

    public StoreRoomBean() {
    }

    public StoreRoomBean(int id, String name, String place, String date_created) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.date_created = date_created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}
