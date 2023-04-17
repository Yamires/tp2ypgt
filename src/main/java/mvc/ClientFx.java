package mvc;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * La classe principale de l'application d'inscription de cours de l'UDEM basée sur JavaFX.
 */
public class ClientFx extends Application {
    /**
     * Lance l'application en chargeant la vue FXML et en affichant la fenêtre principale.
     * @param primaryStage la fenêtre principale de l'application
     */
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
    /**
     * Point d'entrée pour lancer l'application.
     * @param args les arguments de ligne de commande (non utilisés dans cette application)
     */
    public static void main(String[] args){
        launch(args);
    }
}
