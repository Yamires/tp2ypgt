package mvc;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;

public class Controller{

    private ClientFx modele;
    private Vue vue;

    public Controller(ClientFx clientFx, Vue vue) throws IOException, ClassNotFoundException {
        this.modele = clientFx;
        this.vue = vue;

        // charger la session choisie
        this.vue.charger.setOnAction(event -> {

            String selectedSession = String.valueOf(this.vue.getComboBox());
                if (selectedSession != null) {
                    ObservableList<Course> courses = null;
                    try {
                        courses = (ObservableList<Course>) modele.loadCourse(selectedSession);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    this.vue.tableView.setItems(courses);}
        });


        // inscription aux cours
        this.vue.setInscriptionButtonHandler(event -> {
            String prenom = String.valueOf(this.vue.getPrenom());
            String nom = String.valueOf(this.vue.getNom());
            String email = String.valueOf(this.vue.getEmail());
            String matricule = String.valueOf(this.vue.getMatricule());
            Course selectectedCourse = this.vue.getSelectedCourse();

            if (selectectedCourse != null) {
                RegistrationForm form = new RegistrationForm(prenom, nom, email, matricule, selectectedCourse);
                String response = this.modele.register(form);
                this.vue.displayMessage(response);
            } else {
                this.vue.displayError("Le code du cours est invalide.");
            }
        });

    }

}

