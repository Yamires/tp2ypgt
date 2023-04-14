package mvc;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import server.models.Course;
import server.models.RegistrationForm;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * La classe Controller gère les entrées de l'utilisateur et met à jour le modele et la vue.
 */
public class Controller implements Initializable {


    @FXML
    private TableView tableView;
    @FXML
    private TableColumn codeColumn;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Button charger;
    @FXML
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;
    @FXML
    private TextField textField4;
    @FXML
    private Button envoyer;
    @FXML
    private Label messageBox;


    @FXML
    public void onButtonClickedCharger(ActionEvent event) throws IOException, ClassNotFoundException {
        try {
            String selectedSession = comboBox.getValue();
            Modele modele = new Modele("localhost", 1337);
            modele.connect();

            System.out.println(selectedSession);
            ObservableList<Course> courses = FXCollections.observableArrayList(modele.loadCourses(selectedSession));
            System.out.println(courses);
            codeColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
            tableView.setItems(courses);

            modele.disconnect();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void onButtonClickedEnvoyer(ActionEvent event) throws IOException, ClassNotFoundException {
        Modele modele = new Modele("localhost", 1337);

        Course selectedCourse = (Course) tableView.getSelectionModel().getSelectedItem();
        String prenom = textField1.getText();
        String nom = textField2.getText();
        String email = textField3.getText();
        String matricule = textField4.getText();
        int sizeMatricule = matricule.length();
        int size = 6;
        Modele.EmailValidator validator = new Modele.EmailValidator();
        System.out.println(validator.validate(email));


        if (sizeMatricule == 6) {
            if (validator.validate(email)) {

                if (selectedCourse != null) {
                    RegistrationForm form = new RegistrationForm(prenom, nom, email, matricule, selectedCourse);//cast !
                    modele.connect();
                    String response = modele.register(form);
                    Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                    confirmation.setTitle("Confirmation");
                    confirmation.setContentText("Félicitation ! Inscription réussie de " + prenom + "au cours " + selectedCourse.getCode());
                    confirmation.showAndWait();
                    modele.disconnect();
                } else {
                    Alert confirmation = new Alert(Alert.AlertType.ERROR);
                    confirmation.setTitle("Confirmation");
                    confirmation.setContentText("Veuillez sélectionner un cours");
                    confirmation.showAndWait();
                }

            }else {
                Alert confirmation = new Alert(Alert.AlertType.ERROR);
                confirmation.setTitle("Erreur!");
                confirmation.setContentText("email invalide!");
                confirmation.showAndWait();}
        } else {
            Alert confirmation = new Alert(Alert.AlertType.ERROR);
            confirmation.setTitle("Erreur!");
            confirmation.setContentText("Matricule invalide!");
            confirmation.showAndWait();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            comboBox.setItems(FXCollections.observableArrayList("Automne", "Hiver", "Été"));


    }
}