package main.java.dbconnection;

import main.java.model.MyTopic;
import main.java.model.MyWord;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

//this is the main connection to the dictionary db and all the necessary methods
public class dbconnection {
    Connection con = null;

    //Info about the DataBase:
    //Table Name: dictionarydb
    //Fields:
    // id,german,english,pos,topic
    public String getConnectedToDB() {
        //Let's get connected
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(
                    "jdbc:sqlite:dictionarydb.db");
        } catch (Exception e) {
            return "Exception in connection to db";
        }
        return null;
    }
    //this search for the word which might result in null,1 word or 1000 words
    //@params word to search, topic, pos
    //@returns arraylist of Myword class
    public ArrayList<MyWord> searchfortheword(String lang,String word, String topic, String pos) {
        //First Lets get Connected
        String e1 = getConnectedToDB();
        if (e1 != null) return null;
        //now let's ge the result
        try {
            //replace the word and topic and word and get the final sql
            String sql = "select * from dictionary where \"" + lang + "\" like \"" + word + "\" and topic like \"" + topic + "\" and pos like \"" + pos + "\"";
            //for checking to see if the sql is correct
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            //create an arraylist to add the reuslt in there in form of MyWord
            ArrayList<MyWord> wordssearchresult = new ArrayList<>();
            while (rs.next()) {
                wordssearchresult.add(new MyWord(rs.getInt("ID"),
                        rs.getString("german"),
                        rs.getString("english"),
                        rs.getString("pos"),
                        rs.getString("topic")
                ));
            }
            con.close();
            //return the results
            return wordssearchresult;
        } catch (SQLException e) {
            e.printStackTrace();
            con = null;
            return null;
        }
    }




    public ArrayList<MyWord> searchformultipletheword(ArrayList<Integer> listofids) {
        //First Lets get Connected
        String e1 = getConnectedToDB();
        if (e1 != null) return null;
        //now let's ge the result
        try {
            ArrayList<MyWord> multiwordssearchresult = new ArrayList<>();
            //replace the word and topic and word and get the final sql
            for (int i = 0; i <listofids.size() ; i++) {
                PreparedStatement stmt = con.prepareStatement("select * from dictionary where id =?");
                stmt.setInt(1,listofids.get(i));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    multiwordssearchresult.add(new MyWord(rs.getInt("ID"),
                            rs.getString("german"),
                            rs.getString("english"),
                            rs.getString("pos"),
                            rs.getString("topic")
                    ));
                }
            }
            con.close();
            //return the results
            return multiwordssearchresult;
        } catch (SQLException e) {
            e.printStackTrace();
            con = null;
            return null;
        }
    }

    //get all the topics in database
    //return an arraylist of topics
    public ArrayList<MyTopic> getalltopics(){
        //First Lets get Connected
        String e1 = getConnectedToDB();
        if (e1 != null) return null;
        //now let's ge the result
        try {
            String sql = "select * from topics";

            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ArrayList<MyTopic> alltopics = new ArrayList<>();
            while (rs.next()) {
                alltopics.add(new MyTopic(rs.getInt("id"),rs.getString("abbr"),rs.getString("topic")));
            }
            con.close();
            return alltopics;
        } catch (SQLException e) {
            e.printStackTrace();
            con = null;
            return null;
        }
    }

}
