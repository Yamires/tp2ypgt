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

/**
 * La classe Client permet de créer un utlisateur qui se connecte à un serveur pour charger les cours et s'inscrire.
 */
public class ClientFx {
    private final String hostname;
    private final int port;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    /**
     * Constructeur de la classe Client.
     * @param hostname Le nom d'hôte du serveur
     * @param port Le numéro de port sur lequel le serveur écoute
     */
    public ClientFx(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }
    /**
     * Se connecte au serveur en utilisant le nom d'hôte et le port spécifiés dans le constructeur.
     * @throws IOException Si une erreur de connexion se produit
     */
    public void connect() throws IOException {
        socket = new Socket(hostname,port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Se déconnecte du serveur et ferme les flux et la socket.
     * @throws IOException Si une erreur de déconnexion se produit
     */
    public void disconnect() throws IOException{
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
    }

    /**
     * Charge les cours pour la session spécifiée.
     * @param session La session pour laquelle charger les cours
     * @return Une liste des cours disponibles pour la session
     * @throws IOException Si une erreur de communication avec le serveur se produit
     * @throws ClassNotFoundException Si la classe des objets reçus n'est pas trouvée
     */
    public ArrayList<Course> loadCourse(String session) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject("CHARGER" + session);
        objectOutputStream.flush();
        String result = (String) objectInputStream.readObject();
        if ("SUCCESS".equals(result)){
            return (ArrayList<Course>) objectInputStream.readObject();}
        else {
            throw new IOException(("Erreur lors du chargement des cours"));
        }
    }

    // inscription
    public String register(RegistrationForm form) throws IOException, ClassNotFoundException{
        objectOutputStream.writeObject("INSCRIRE");
        objectOutputStream.flush();
        objectOutputStream.writeObject(form);
        objectOutputStream.flush();
        return (String) objectInputStream.readObject();
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


