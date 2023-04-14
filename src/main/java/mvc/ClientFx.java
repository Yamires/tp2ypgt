package mvc;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ClientFx extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(new URL("file:/Users/yamira.poldosilva/Desktop/tp2ypgt/src/main/java/mvc/view.fxml"));
            primaryStage.setTitle("Inscription UdeM");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (NullPointerException | IOException e) {
            System.out.println("t");
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        launch(args);
    }
}
