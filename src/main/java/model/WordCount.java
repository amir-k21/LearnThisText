package main.java.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
    //this is the model for my wordcount table which has word and counts of the words
public class WordCount {
    private SimpleStringProperty word;
    private SimpleIntegerProperty wordcount;

    public WordCount(String word, int wordcount) {
        this.word = new SimpleStringProperty(word);
        this.wordcount = new SimpleIntegerProperty(wordcount);
    }

    public String getWord() {
        return word.get();
    }

    public SimpleStringProperty wordProperty() {
        return word;
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public int getWordcount() {
        return wordcount.get();
    }

    public SimpleIntegerProperty wordcountProperty() {
        return wordcount;
    }

    public void setWordcount(int wordcount) {
        this.wordcount.set(wordcount);
    }
}
