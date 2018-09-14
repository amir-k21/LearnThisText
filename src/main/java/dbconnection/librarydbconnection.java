package main.java.dbconnection;

import main.java.model.MyText;
import main.java.model.MyTopic;
import main.java.model.MyWord;

import java.awt.geom.RectangularShape;
import java.sql.*;
import java.util.ArrayList;

//this is the connection and all the necessary methods for mylibrary database
public class librarydbconnection {
    Connection con = null;

    //Info about the DataBase:
    //there are two tables:
    //1- Table Name: wordintext
    //Fields:
    // textid, wordid   both integer not null
    //1- Table Name: texttable
    //Fields:
    // id, text(not null)
        //** id 1 text other
    public String getConnectedToDB() {
        //Let's get connected
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(
                    "jdbc:sqlite:mylibrary.db");
        } catch (Exception e) {
            return "Exception in connection to db";
        }
        return null;
    }

    //gets title and txt
    //@Returns the id of the text
    public int saveTexttoDB(String text){
        //first get connected
        String el = getConnectedToDB();
        if (el!=null)return 0;

        try {
            //now lets insert into db
            PreparedStatement stmt = con.prepareStatement("INSERT INTO texttable(text) VALUES(?)");
            stmt.setString(1,text);

            int i = stmt.executeUpdate();

            //now if it has been inserted let's return the id of the last inserted text
            if (i == 1){
                PreparedStatement stmtgetid = con.prepareStatement("SELECT last_insert_rowid();");
                ResultSet rs = stmtgetid.executeQuery();

                if(rs.next()){

                    int id = rs.getInt(1);
                    con.close();
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            con = null;
            return 0;
        }

        con=null;
        return 0;
    }

    public int saveWordandTextID(int textID,int wordID) {
        //first get connected
        String el = getConnectedToDB();
        if (el != null) return 0;

        //now add the data to wordintext table
        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO wordintext(textid,wordid) VALUES(?,?)");
            stmt.setInt(1,textID);
            stmt.setInt(2,wordID);
            int i = stmt.executeUpdate();
            con.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
            con=null;
            return 0;
        }


    }

    public ArrayList<MyWord> getAllWordsofLibraryofText(int textid){
        //first get connected
        String el = getConnectedToDB();
        if (el != null) return null;

        ArrayList<Integer> wordids = null;
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * from wordintext where textid=?");
            stmt.setInt(1,textid);
            ResultSet rs = stmt.executeQuery();
            wordids = new ArrayList<>();
            while (rs.next()){
                wordids.add(rs.getInt("wordid"));
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            con=null;
            return null;
        }
        dbconnection dbconn = new dbconnection();
        ArrayList<MyWord> listofwords = dbconn.searchformultipletheword(wordids);
        return listofwords;
    }


    public ArrayList<MyText> getAllTextsFromLibrary(){
        //first get connected
        String el = getConnectedToDB();
        if (el != null) return null;

        //now get the data from texttable
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * from texttable");
            ResultSet rs = stmt.executeQuery();
            ArrayList<MyText> alltexts = new ArrayList<>();
            while (rs.next()) {

                alltexts.add(new MyText(rs.getInt("id"),rs.getString("text")));
            }

            con.close();
            return alltexts;
        } catch (SQLException e) {
            e.printStackTrace();
            con= null;
            return null;
        }
    }

    public int deleteOneWordFromDB(int wordID) {
        //first get connected
        String el = getConnectedToDB();
        if (el != null) return 0;

        //now delete a word from db
        try {
            PreparedStatement stmt = con.prepareStatement("Delete From wordintext where wordid=?");
            stmt.setInt(1,wordID);
            int i = stmt.executeUpdate();

            con.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
            con=null;
            return 0;
        }
    }

    public int deleteTextAndAllWords(int textid) {
        //first get connected
        String el = getConnectedToDB();
        if (el != null) return 0;

        //now delete a text from db and then all the words related to that text
        try {
            PreparedStatement stmt = con.prepareStatement("Delete From wordintext where textid=?");
            stmt.setInt(1,textid);
            int i = stmt.executeUpdate();

            PreparedStatement stmtother = con.prepareStatement("Delete From texttable where id=?");
            stmtother.setInt(1,textid);
            int j = stmtother.executeUpdate();

            con.close();
            return j;
        } catch (SQLException e) {
            e.printStackTrace();
            con=null;
            return 0;
        }
    }

}
