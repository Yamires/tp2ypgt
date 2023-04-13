
package mvc;




import javafx.collections.ObservableList;
import javafx.geometry.Orientation;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


import server.models.Course;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * La classe Vue gère les éléments visuels du Client et les actions sur l'interface graphique.
 */
public class Vue extends VBox {


    //  MENU 1 TABLEVIEW
    private Text titre = new Text("Liste des cours");
    TableView<Course> tableView = new TableView<>();

    //première colonne
    TableColumn<Course, String> codeColumn = new TableColumn<>("Code");

    //deuxième colonne
    TableColumn<Course, String> courseColumn = new TableColumn<>(("Cours"));

    // MENU 2 SELECTIONNEUR DE SESSION

    private ComboBox comboBox = new ComboBox();
    Button charger = new Button("Charger");


    // MENU 3 FORMULAIRE D'INSCRIPTION
    private Text titre2 = new Text("Formulaire d'inscription");
    private Text soustitre1 = new Text("Prénom");
    private TextField prenom = new TextField();
    private Text soustitre2 = new Text("Nom");
    private TextField nom = new TextField();
    private Text soustitre3 = new Text("Email");
    private TextField email = new TextField();
    private Text soustitre4 = new Text("Matricule");
    private TextField matricule = new TextField();
    Button envoyer = new Button("envoyer");


    // handles position des élements
    public Vue() {


        VBox root = new VBox();

        // menu 1

        VBox menu1 = new VBox();


        menu1.getChildren().add(titre);

        tableView.getColumns().add(codeColumn);
        tableView.getColumns().add(courseColumn);

        menu1.getChildren().add(tableView);




        // menu 2
        Separator separator1 = new Separator();

        VBox menu2 = new VBox();

        menu2.getChildren().add(charger);

        comboBox.getItems().add("Automne");
        comboBox.getItems().add("Hiver");
        comboBox.getItems().add("Été");

        menu2.getChildren().add(comboBox);

        // menu3

        Separator separator2 = new Separator(Orientation.VERTICAL);
        VBox menu3 = new VBox();

        menu3.getChildren().add(titre2);

        HBox line1 = new HBox();

        line1.getChildren().add(soustitre1);
        line1.getChildren().add(prenom);


        HBox line2 = new HBox();

        line2.getChildren().add(soustitre2);
        line2.getChildren().add(nom);


        HBox line3 = new HBox();

        line3.getChildren().add(soustitre3);
        line3.getChildren().add(email);

        HBox line4 = new HBox();


        line4.getChildren().add(soustitre4);
        line4.getChildren().add(matricule);

        HBox line5 = new HBox();


        line5.getChildren().add(envoyer);


        // root
        root.getChildren().add(menu1);
        root.getChildren().add(menu2);
        root.getChildren().add(menu3);



        // controls

        }

        // tableview

        //  menu contextuelle
        public ComboBox getComboBox() {return (ComboBox) comboBox.getValue();}

        public Button setCharger() {return this.charger;}



        // formulaire inscription // menu 3
        public TextField getPrenom() {return prenom;}

        public TextField getNom() {return nom;}

        public TextField getEmail() {return email;}

        public TextField getMatricule() {return matricule;}



    // récuperer le cours choisi
    public Course getSelectedCourse(){
            ObservableList<Integer> selectedIndices = tableView.getSelectionModel().getSelectedIndices();
        return null;
    };


    //TODO
    // message de reussite
    public void displayMessage(String response) {}

    public void displayError() {
    }
    //TODO
    // message d'erreur


    //public void displayError() {
    }






