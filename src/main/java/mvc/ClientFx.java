package mvc;

import javafx.scene.Scene;
import javafx.stage.Stage;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientFx {
    private final String hostname;
    private final int port;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientFx(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(hostname,port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }
    public void disconnect() throws IOException{
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
    }

    // CHARGE LES COURS POUR LA SESSION SELECTIONNÉE
    public ArrayList<Course> loadCourse(String session) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject("Charger" + session);
        String result = (String) objectOutputStream.readObject();
        if ("SUCCESS".equals(result)){
            return (ArrayList<Course>) objectInputStream.readObject();}
        else {
            throw new IOException(("Erreur lors du chargemeent des cours"));
        }
    }
    // affichage des cours dispaonibles --> transformer en interface graphique
    public ArrayList<Course> displayCoursesForSession (String [] cours, Scanner scanner) throws IOException, ClassNotFoundException {
        int session = scanner.nextInt();
        ArrayList<Course> courses = loadCourse(cours[session - 1]);
        int i = 1;
        System.out.println("Cours disponibles pendant la session d'"+ cours[session - 1] + ":");
        for (Course course : courses) {
            System.out.println(i + "." + course.getCode() + "-" + course.getName());
            i++;
        }
        scanner.nextLine();
        return courses;
    }

    // choisir session et afficher session

    public ArrayList<Course> menu(Scanner scanner) throws IOException, ClassNotFoundException{
        ArrayList<Course> courses = null;
        System.out.print("veuillez choisir la session pour laquelle consulter la liste des cours: \n1. Automne \n2. Hiver \n Été \n> choix:");
        String[] course = {"Automne", "Hiver", "Été"};
        courses = displayCoursesForSession(course, scanner);

        int choice;

        while (true) {
            System.out.println("Choix \n1. choisir une session \n2. ajouter un cours \n> Choix: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1){
                System.out.println(("Veuilles choisir la session pour laquelle consuter la liste des cours: \n1. Automne \n2. Hiver \n3. Eté \n> Choix: "));
                connect();
                courses = displayCoursesForSession(course, scanner);}
            else if (choice == 2){
                break;}
            else { System.out.println("choix invalide, veuillez réessayer");
            }
        }
        return courses;
    }


    // inscription
    public String register(RegistrationForm form) throws IOException, ClassNotFoundException{
        objectOutputStream.writeObject("INSCRIRE");
        objectOutputStream.flush();
        objectOutputStream.writeObject(form);
        objectOutputStream.flush();
        return (String) objectOutputStream.readObject();
    }
    // chercher cours avec code du cours
    public Course searchCourse(ArrayList<Course> courses, Scanner scanner){
        System.out.print("Entres le code du cours auquel vous souhaitez vous inscrire:");
        String codeCours = scanner.nextLine();

        Course selectedCourse = null;
        for(Course course : courses) {
            if (course.getCode().equals(codeCours)){
                selectedCourse = course;
                break;
            }
        }
        return selectedCourse;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        ClientFx modele = new ClientFx("localhost", 1337);

        modele.connect();

        Vue vue = new Vue();
        Controller controller = new Controller(modele, vue);

        Scene scene = new Scene(vue, 400, 500);

        Stage stage = null;
        stage.setScene(scene);
        stage.setTitle("Inscription UDEM");
        stage.show();

        modele.disconnect();

    }

}


