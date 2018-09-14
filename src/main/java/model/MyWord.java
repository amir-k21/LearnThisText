package main.java.model;

// this is the model for my words which have id german english pos and topic
public class MyWord {
    private int id;
    private String german;
    private String english;
    private String pos;
    private String topic;

    public MyWord(int id, String german, String english, String pos, String topic) {
        this.id = id;
        this.german = german;
        this.english = english;
        this.pos = pos;
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGerman() {
        return german;
    }

    public void setGerman(String german) {
        this.german = german;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
