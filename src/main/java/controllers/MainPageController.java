package main.java.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.java.dbconnection.dbconnection;
import main.java.dbconnection.librarydbconnection;
import main.java.model.MyText;
import main.java.model.MyTopic;
import main.java.model.MyWord;
import main.java.model.WordCount;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private Label statuslbl;

    final ToggleGroup togglegroup = new ToggleGroup();
    //Observable list for the result of the searches
    ObservableList<MyWord> searchresultobservablelist = FXCollections.observableArrayList();
    //array of all the topics in form of Mytopic object
    ArrayList<MyTopic> getalltopicsarraylist;
    //array of all the words that are the result of the search of MYWORD
    ArrayList<MyWord> searchresultarraylist;
    librarydbconnection libdbcon = new librarydbconnection();
    //when load a text to webview it adds it to db
    //we set the defult to 1 because 1 is other.
    int loadedtextid = 1;
    ObservableList<MyText> textfromlibraryobservablelist = FXCollections.observableArrayList();
    @FXML
    private TableView<WordCount> usedvocabulariestable;
    @FXML
    private TableColumn<WordCount, String> uservocabwordcol;
    @FXML
    private TableColumn<WordCount, Integer> usedvocabcountcol;
    //observable list for the table
    private ObservableList<WordCount> countedword = FXCollections.observableArrayList();
    //observable list for the words in text that are already in my library
    private ObservableList<MyWord> wordinlibraryobservablelist = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> partofspeachcombobox;
    @FXML
    private ComboBox<String> topiccombobox;
    @FXML
    private TextField wordtosearchtxt;
    @FXML
    private ListView<MyWord> mywordintextlistview;
    @FXML
    private ListView<MyWord> searchresultlist;
    @FXML
    private TextArea onewordresulttxt;
    @FXML
    private RadioButton entoderadiobtn;
    @FXML
    private RadioButton detoenradiobtn;
    //my db connection
    private dbconnection dbcon = new dbconnection();
    //these texts will be shown on the webview hightlighted or not
    private String texttoread;
    private String highlightedtexttoread;
    @FXML
    private WebView showtextwebview;
    private WebEngine webengine;
    @FXML
    private Button choosefilebtn;
    @FXML
    private Button addtolibbtn;
    @FXML
    private ComboBox<MyText> choosefromlibrarycombobox;
    @FXML
    private Button deletetextfromlibbtn;

    //**********************************//
    //instead of initializing the table we initialize it after copying a text
    private void getTheTableReady(){
        //set the cell value factory for the table
        uservocabwordcol.setCellValueFactory(new PropertyValueFactory<WordCount,String>("word"));
        usedvocabcountcol.setCellValueFactory(new PropertyValueFactory<WordCount,Integer>("wordcount"));
        //set the items for the table
        usedvocabulariestable.setItems(countedword);
    }

    //Load the TexttoRead into the Webview
    private void loadTextToWebView(){
        webengine = showtextwebview.getEngine();
        webengine.loadContent("<html><head></head><body >"+ texttoread +" </body></html>");
        showtextwebview.setContextMenuEnabled(false);

        statuslbl.setText("text is Loaded.");
    }

    //Load the highlighted text to the Webview
    private void loadhighilghtedTextToWebView(){
        webengine = showtextwebview.getEngine();
        webengine.loadContent("<html><head></head><body >"+ highlightedtexttoread +" </body></html>");
        showtextwebview.setContextMenuEnabled(false);

        statuslbl.setText("text is Loaded.");
    }

    //initialize the controller and add options to topic combo box and POS combobox
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        long starttime = System.currentTimeMillis();
        //initialize the libtexts
        initializeLibraryCombobox();
        loadTextsToLibComboBox();
        //initialize the webview for test
        texttoread =choosefromlibrarycombobox.getValue().getText();
        countTheWords(texttoread);
        loadTextToWebView();
        //initialize the table for test
        getTheTableReady();
        //initailize the toggle group for radio buttons
        detoenradiobtn.setToggleGroup(togglegroup);
        entoderadiobtn.setToggleGroup(togglegroup);

        topiccombobox.getItems().add(null);
        //get items from the database
        //it's a hashmap
        getalltopicsarraylist = dbcon.getalltopics();
        //check for null because if there was any problem in dbconnection it would return null
        if(getalltopicsarraylist !=null){
            for (int i = 0; i < getalltopicsarraylist.size() ; i++) {
                topiccombobox.getItems().add(getalltopicsarraylist.get(i).getTopic());
            }
        }
        //set the options for pos
        partofspeachcombobox.getItems().addAll(null,"noun","verb","adj","adv",
                "past-p","prep","conj","pron","prefix","suffix",
                "pres-p","past-p");
        //initialize the word in library listview
        mywordintextlistview.setItems(wordinlibraryobservablelist);
        mywordintextlistview.setCellFactory((ListView<MyWord> lv) ->
                new ListCell<MyWord>() {
                    @Override
                    public void updateItem(MyWord myword, boolean empty) {
                        super.updateItem(myword, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            // use whatever data you need from the MyWord
                            // object to get the correct displayed value:
                            setText(myword.getGerman());
                        }
                    }
                }
        );
        loadWordinLibraryListView();
        //initialize the searchresultlist
        searchresultlist.setItems(searchresultobservablelist);
        //initialize the cell style  a listener to see the german part of the words
        searchresultlist.setCellFactory((ListView<MyWord> lv) ->
                new ListCell<MyWord>() {
                    @Override
                    public void updateItem(MyWord myword, boolean empty) {
                        super.updateItem(myword, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            // use whatever data you need from the MyWord
                            // object to get the correct displayed value:
                            setText(myword.getGerman());
                        }
                    }
                }
        );
        //some notes to myself
        ///TEST add listener to the probperty of list **it works great
//        searchresultlist.getSelectionModel().selectedItemProperty()
//                .addListener((v,ov,nv)-> {
//                    System.out.println(v.getValue().getGerman());
//                });
        //instead of mouse event set listener for list view to show the information in the text view

        searchresultlist.getSelectionModel().selectedItemProperty()
                .addListener((v,ov,nv)-> {
                    //do something here
                    //check if there is any new value
                    if (nv!=null){
                        addtolibbtn.setDisable(false);
                        onewordresulttxt.setText(
                                searchresultlist.getSelectionModel().getSelectedItem().getPos()+"\n"+
                                        searchresultlist.getSelectionModel().getSelectedItem().getEnglish()+"\n"+
                                        searchresultlist.getSelectionModel().getSelectedItem().getTopic()

                        );
                    }else{//it means there is no other value so just make the textarea empty
                        onewordresulttxt.setText("");
                        addtolibbtn.setDisable(true);
                    }
                });



        //some notes to myself
        //it's very important and i had to go through so much search
        //my reference is the link below:
        //https://stackoverflow.com/questions/27118872/how-to-hide-tableview-column-header-in-javafx-8
        /*
        ListView<Album> albumList = new ListView<>();
        albumList.setCellFactory((ListView<Album> lv) ->
            new ListCell<Album>() {
                @Override
                public void updateItem(Album album, boolean empty) {
                    super.updateItem(album, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        // use whatever data you need from the album
                        // object to get the correct displayed value:
                        setText(album.getTitle());
                    }
                }
            }
        );
         */
        long finishtime = System.currentTimeMillis();
//        System.out.println(finishtime-starttime + "milli sec");
        //the initialization normally takes between 500 to 900 millisec

        statuslbl.setText("");
    }

    //Load words to the list view (the one on write that shows the words that i'm learning from a text)
    private void loadWordinLibraryListView() {
        ArrayList<MyWord> listofwordinlib = libdbcon.getAllWordsofLibraryofText(loadedtextid);
        //clear the oblist so you wouldn't get dublicates
        wordinlibraryobservablelist.clear();
        for (int i = 0; i <listofwordinlib.size() ; i++) {
            wordinlibraryobservablelist.add(listofwordinlib.get(i));
        }
        statuslbl.setText("related words to this text have been loaded.");
    }

    //first we initialize everything necessary for the combobox and later we load it.
    private void initializeLibraryCombobox() {

        choosefromlibrarycombobox.setItems(textfromlibraryobservablelist);
        //THIS Solutions works except it set's the value to that as well.
//        choosefromlibrarycombobox.setCellFactory((ListView<MyText> lv) ->
//                new ListCell<MyText>() {
//                    @Override
//                    public void updateItem(MyText myText, boolean empty) {
//                        super.updateItem(myText, empty);
//                        if (empty) {
//                            setText(null);
//                        } else {
//                            // use whatever data you need from the MyWord
//                            // object to get the correct displayed value:
//                            if(myText.getText().length()>30){
//                                setText(myText.getText().substring(0,29)+ "...");
//                            } else{
//                                setText(myText.getText());
//                            }
//
//                        }
//                    }
//                }
//        );

        //so let's try this one
        //source https://stackoverflow.com/questions/35744227/javafx-8-combobox-and-observablelist
        ///This one works great!
        //we set the combobox to show the text in the object and not the object itself
        choosefromlibrarycombobox.setConverter(new StringConverter<MyText>() {
            @Override
            public String toString(MyText object) {
                if(object.getText().length()>100){
                    return object.getText().substring(0,100)+ "...";
                } else{
                    return object.getText();
                }

            }

            @Override
            public MyText fromString(String string) {
                return null;
            }
        });
    }

    //this method gets a text and counts the words of it.
    //@requires text
    private void countTheWords(String text){
        HashMap<String,Integer> counthis = new HashMap<>();
//        text.replace("</br>","-");
        String[] counted = text.split("[\\n\\s\\d!\"#$%&\'()*+,-./:;\\\\<>=?@\\^_`{}|~\\[\\]]");
        for (int i = 0; i <counted.length ; i++) {
            if (counthis.containsKey(counted[i])){
                counthis.put(counted[i],counthis.get(counted[i])+1);
            }else{
                if ((!counted[i].equals("\\n"))&&(!counted[i].equals("br"))&&(!counted[i].equals(""))){
                    counthis.put(counted[i],1);
                }
            }
        }
        //clear the list so after each text there would be new word count
        countedword.clear();
        for(String key: counthis.keySet()){
//            System.out.println(key + "  "+ counthis.get(key));
            countedword.add(new WordCount(key,counthis.get(key)));
        }

        statuslbl.setText("counted the words.");
    }

    @FXML
    void searchTheWord(ActionEvent event) {
        //get the language (defult is german otherwise switch)
        String lang ="german";
        if(entoderadiobtn.isSelected()){
            lang="english";
        }
        //get the word and clean up for any possible sql injeciton
        String word = cutEverything(wordtosearchtxt.getText());
        //set the topic and if topic was chosen change it to the chose topic
        String topic = "%%";
        //it should get the abbr not the topic itself
        if (topiccombobox.getValue()!=null) topic = "%" + getTheTopicAbbr(topiccombobox.getValue()) + "%";
        //set the pos and if pos was chosen change it to the chose pos
        String pos = "%%";
        if (partofspeachcombobox.getValue()!=null) pos = "%" + partofspeachcombobox.getValue() + "%";
        //save the result in the array list
        searchresultarraylist = dbcon.searchfortheword(lang,word,topic,pos);
        //now it's time to show on the listview
        //we have the observable list
        //and we have initialized the observable list to the list
        //now we just need to add the words to the observable list
        //first clean everything from it
        searchresultobservablelist.clear();
        for (int i = 0; i < searchresultarraylist.size(); i++) {
            searchresultobservablelist.add(searchresultarraylist.get(i));
        }

        statuslbl.setText("Searched the Word");
    }

    //finds the abbriviation of a topic
    //@requires a topic
    //@returns abbrivaition of it.
    private String getTheTopicAbbr(String value) {
        //it takes 0 millisec to find the abbr
        String abbr ="";
        for (int i = 0; i <getalltopicsarraylist.size() ; i++) {
            if (getalltopicsarraylist.get(i).getTopic().equals(value)){
                abbr = getalltopicsarraylist.get(i).getAbbr();
            }
        }
        return abbr;
    }

    @FXML
    void closeTheProgram(ActionEvent event) {
        statuslbl.setText("closing the program ...");
        ((Stage) choosefilebtn.getScene().getWindow()).close();
    }

    //clean up the text to secure from any sql injection
    private String cutEverything(String text) {
        String cut = text.replaceAll("[\\d!\"#$&\'()*+,./:;\\\\<>=?@\\^`{}|~\\[\\]]","");
        return cut;
    }

    @FXML
    void PasteTextToShowOnWebview(ActionEvent event) {
        //resource https://code.makery.ch/blog/javafx-dialogs-official/
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Paste your text here.");
        dialog.setHeaderText(null);

        ButtonType OkButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OkButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextArea copyarea = new TextArea();
        copyarea.setWrapText(true);
        grid.add(copyarea, 0, 0);

        dialog.getDialogPane().setPadding(new Insets(10,10,10,10));
        dialog.getDialogPane().setContent(grid);


        // Convert the result to a get the text of the textarea when the ok button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OkButtonType) {
                return copyarea.getText();
            }
            return null;
        });

        //if there is a result and ok button has been clicked
        //then set the  texttoread to this and load the webview
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ttt->{
//            System.out.println(ttt.toString());
//            System.out.println("it presents");
            texttoread = result.get().replaceAll("\n","<br/>");
            countTheWords(texttoread);

            //add the text to db and change the sored text id to the id of the newly added
            loadedtextid = libdbcon.saveTexttoDB(texttoread);
            statuslbl.setText("text has been copied.");
            loadTextsToLibComboBox();
            loadTextToWebView();
            loadWordinLibraryListView();
        });
        statuslbl.setText("loaded the text.");
    }

    @FXML
    void SearchThisWordInDictionary(ActionEvent event) {
        String word = "%" + usedvocabulariestable.getSelectionModel().getSelectedItem().getWord() + "%";
        wordtosearchtxt.setText(word);
        statuslbl.setText("searched the word in dictionary.");
        searchTheWord(event);
    }

    @FXML
    void chooseFileToShowOnWebview(ActionEvent event) {
        //create a new file chooser
        FileChooser fileChooser = new FileChooser();
        //Set extension filter for our file chooser
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        //first get the scene so we could show a file chooser dialogue on the stage
        Stage stage = (Stage) choosefilebtn.getScene().getWindow();
        File file = null;
        file = fileChooser.showOpenDialog(stage);
        //if there is a file selected continue and process the saving
        if(file != null) {
            if (readfile(file) != null) {
                texttoread = readfile(file);
                //then immidiately set the file to null so we wouldn't override the poor file
                countTheWords(texttoread.replace("</br>",""));

                //add the text to db and change the sored text id to the id of the newly added
                loadedtextid = libdbcon.saveTexttoDB(texttoread);
            } else {
                //set the message directly on the html editor.
                //you could make a label on the bottom
                //but it looks more fun :)
                statuslbl.setText("Sorry Couldn't open your file!");
                texttoread = "";
            }
            loadTextsToLibComboBox();
            loadTextToWebView();
            loadWordinLibraryListView();
        }

    }

    //a method to read the file
    private String readfile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader;
        try {
            //reference https://stackoverflow.com/questions/9281629/read-special-characters-in-java-with-bufferedreader
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO-8859-1"));
            String text;
            while((text = bufferedReader.readLine())!=null){
                stringBuilder.append(text + "<br/>");
            }
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    @FXML
    void highlightWordInText(ActionEvent event) {
        String word = usedvocabulariestable.getSelectionModel().getSelectedItem().getWord();
//        System.out.println(usedvocabulariestable.getSelectionModel().getSelectedItem().getWord());
        String wordtohighligh = "<span style=\"background-color: #FFFF00\">"+word+"</span>";
        highlightedtexttoread = texttoread.replaceAll("\\b"+word+"\\b",wordtohighligh);
        loadhighilghtedTextToWebView();
    }

    //just adds the id of the word and related text to the db and later we fetch the full data
    @FXML
    void addTheWordtoLibrary(ActionEvent event) {
        //get the word id
        int wordid = searchresultlist.getSelectionModel().getSelectedItem().getId();
        //get the text id loadedtextid
        //save to db
        libdbcon.saveWordandTextID(loadedtextid,wordid);
        loadWordinLibraryListView();
        statuslbl.setText("word has been added to the library.");
    }

    //get all texts to show in combobox
    public void loadTextsToLibComboBox(){
//        System.out.println("we want to get the arraylist of all the texts");
        ArrayList<MyText> alllibtexts = libdbcon.getAllTextsFromLibrary();
//        System.out.println(" we got it");
//        System.out.println("array list = " + alllibtexts);
//        System.out.println("now we want to clear the oblist");
        textfromlibraryobservablelist.clear();
        for (int i = 0; i <alllibtexts.size() ; i++) {
            textfromlibraryobservablelist.add(alllibtexts.get(i));
        }
        int lastmember = textfromlibraryobservablelist.size()-1;
        choosefromlibrarycombobox.setValue(textfromlibraryobservablelist.get(lastmember));
    }

    //an action after choosing something in combobox so it would load the related text
    @FXML
    public void loadTheChosenTextFromLibrary(ActionEvent event) {
        if(choosefromlibrarycombobox.getValue()==null){
            loadedtextid = 1;
        }else{
            loadedtextid = choosefromlibrarycombobox.getValue().getId();
        }
        loadWordinLibraryListView();
        if(choosefromlibrarycombobox.getValue()!=null){
            texttoread =choosefromlibrarycombobox.getValue().getText();
        }
        countTheWords(texttoread);
        loadTextToWebView();
    }

    @FXML
    void showHelpPage(ActionEvent event) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main/resources/view/HelpPage.fxml"));
            stage.setTitle("HELP");
            stage.initModality(Modality.APPLICATION_MODAL);
            Image icon = new Image(getClass().getClassLoader().getResourceAsStream("main/resources/images/icon.png"));
            stage.getIcons().add(icon);

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void showAboutAlert(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About us");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        Image icon = new Image(getClass().getClassLoader().getResourceAsStream("main/resources/images/icon.png"));
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);
        //to clear all the buttons and the functionality of close buton you should do this
        //but for now we don't need that.
        //alert.getButtonTypes().clear();
        alert.setContentText("I made it for all the people who want to learn a new language by reading texts." +
                "\nThe dictionary behind this software is dict.cc");

        alert.showAndWait();

    }

    @FXML
    void deleteOneTextAndAllFromLibrary(ActionEvent event) {
        if (choosefromlibrarycombobox.getValue() != null){
            int textid = choosefromlibrarycombobox.getValue().getId();

            if(textid != 1){
                int i = libdbcon.deleteTextAndAllWords(textid);
                texttoread ="";
                loadTextToWebView();
                loadWordinLibraryListView();
                countTheWords(texttoread);

                //SOME HOW THIS WOKS THE BEST
                loadTextsToLibComboBox();
//                textfromlibraryobservablelist.remove(choosefromlibrarycombobox.getValue());
//                initializeLibraryCombobox();
//                loadTextsToLibComboBox();
//                loadTextsToLibComboBox();
            }

        }
        statuslbl.setText("text and all related words have been deleted from library.");
    }

    @FXML
    void deleteOneWordfromLibrary(ActionEvent event) {
        int wordid = mywordintextlistview.getSelectionModel().getSelectedItem().getId();
        libdbcon.deleteOneWordFromDB(wordid);
        loadWordinLibraryListView();
        statuslbl.setText("word has been deleted from the library.");
    }

    @FXML
    void setToFullScreen(ActionEvent event) {
    Stage stage = (Stage) statuslbl.getScene().getWindow();
    if(stage.isFullScreen()){
        stage.setFullScreen(false);
    }else {
        stage.setFullScreen(true);
    }

    }

}
