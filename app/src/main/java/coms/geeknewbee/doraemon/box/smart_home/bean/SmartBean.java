package coms.geeknewbee.doraemon.box.smart_home.bean;

/**
 * Created by chen on 2016/4/11
 */
public class SmartBean {
    public int id;
    public String category;
    public String word;
    public String type;
    public String data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", word='" + word + '}';
    }
}
