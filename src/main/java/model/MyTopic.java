package main.java.model;

public class MyTopic {
    private int id;
    private String abbr;
    private String topic;

    //this is the model for topics which havev id, abbriviation and topic
    public MyTopic(int id,String abbr, String topic) {
        this.id = id;
        this.abbr = abbr;
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
