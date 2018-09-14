package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main/resources/view/MainPage.fxml"));
        primaryStage.setTitle("Learn This Text");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(930);
        primaryStage.setMinHeight(660);
        Image icon = new Image(getClass().getClassLoader().getResourceAsStream("main/resources/images/icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
