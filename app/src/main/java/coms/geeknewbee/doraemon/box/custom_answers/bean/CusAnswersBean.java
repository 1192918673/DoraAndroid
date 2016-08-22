package coms.geeknewbee.doraemon.box.custom_answers.bean;

import java.util.List;

/**
 * Created by chen on 2016/3/30
 */
public class CusAnswersBean {

    private int id;
    private String question;
    private String answer;
    private String date_created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}
