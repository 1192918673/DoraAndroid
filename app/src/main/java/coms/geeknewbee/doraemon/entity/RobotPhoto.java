package coms.geeknewbee.doraemon.entity;

import java.io.Serializable;

/**
 * Created by 马庆军 on 16/4/28.
 */
public class RobotPhoto implements Serializable{

    /**
     * id : 55
     * photo_type : 1
     * photo : http://doraemon.microfastup.com/media/photos/2016/04/21/1_6qrmAYT.jpg
     * thumbnail : http://doraemon.microfastup.com/media/photos/2016/04/21/1_6qrmAYT.150x150.jpg
     * date_created : 2016-04-21 16:42:55
     */

    private int id;
    private String photo_type;
    private String photo;
    private String thumbnail;
    private String date_created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto_type() {
        return photo_type;
    }

    public void setPhoto_type(String photo_type) {
        this.photo_type = photo_type;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
