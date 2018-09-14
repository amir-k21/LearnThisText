package main.java.model;

public class MyText {
    private int id;
    private String title;
    private String text;

    //This is the model for my text which only has id and text itself
    public MyText(int id,  String text) {
        this.id = id;

        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
