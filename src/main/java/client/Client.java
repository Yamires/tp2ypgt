
package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import server.models.Course;
import server.models.RegistrationForm;
import java.io.IOException;


/**
 * La classe Client permet de créer un utilisateur qui se connecte à un serveur pour charger les cours et s'inscrire.
 */
public class Client {
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
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Se connecte au serveur en utilisant le nom d'hôte et le port spécifiés dans le constructeur.
     * @throws IOException Si une erreur de connexion se produit
     */
    public void connect() throws IOException {
        socket = new Socket(hostname, port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Se déconnecte du serveur et ferme les flux et le socket.
     * @throws IOException Si une erreur de déconnexion se produit
     */
    public void disconnect() throws IOException {
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
    public ArrayList<Course> loadCourses(String session) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject("CHARGER " + session);
        objectOutputStream.flush();
        String result = (String) objectInputStream.readObject();
        if ("SUCCESS".equals(result)) {
            return (ArrayList<Course>) objectInputStream.readObject(); // On reçoit puis traite la liste de cours
        } else {
            throw new IOException("Erreur lors du chargement des cours");
        }
    }

    /**
     * Envoie un formulaire d'inscription au serveur pour s'inscrire à un cours.
     * @param form Le formulaire d'inscription à envoyer
     * @return Une chaîne de caractères indiquant le résultat de l'inscription
     * @throws IOException Si une erreur de communication avec le serveur se produit
     * @throws ClassNotFoundException Si la classe des objets reçus n'est pas trouvée
     */
    public String register(RegistrationForm form) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject("INSCRIRE");
        objectOutputStream.flush();
        objectOutputStream.writeObject(form);
        objectOutputStream.flush();
        return (String) objectInputStream.readObject();
    }

    /**
     * Affiche les cours disponibles pour la session choisie.
     * @param cours Un tableau contenant les noms des sessions
     * @param scanner Un scanner pour lire l'entrée de l'utilisateur
     * @return Une liste des cours disponibles pour la session choisie
     * @throws IOException Si une erreur de communication avec le serveur se produit
     * @throws ClassNotFoundException Si la classe des objets reçus n'est pas trouvée
     */
    public ArrayList<Course> displayCoursesForSession( String[] cours, Scanner scanner) throws IOException, ClassNotFoundException {
        int session = scanner.nextInt();
        ArrayList<Course> courses = loadCourses(cours[session-1]);
        int i = 1;

        System.out.println("Cours disponibles pendant la session d'" + cours[session-1] + " :");
        for (Course course : courses) {
            System.out.println(i + ". " +  course.getCode() + " - " + course.getName());
            i++;
        }
        scanner.nextLine();
        return courses;
    }

    /**
     * Recherche un cours dans la liste des cours disponibles en utilisant le code du cours saisi par l'utilisateur.
     * @param courses La liste des cours disponibles
     * @param scanner Un scanner pour lire l'entrée de l'utilisateur
     * @return Le cours correspondant au code saisi, ou null si aucun cours ne correspond
     */
    public Course searchCours(ArrayList<Course> courses, Scanner scanner){
        System.out.print("Entrez le code du cours auquel vous souhaitez vous inscrire : ");
        String codeCours = scanner.nextLine();

        Course selectedCourse = null; // On initialise à null au cas où aucun cours n'est trouvé
        for (Course course : courses) {
            if (course.getCode().equals(codeCours)) {
                selectedCourse = course;
                break;
            }
        }
        return selectedCourse;
    }

    /**
     * Affiche un menu pour permettre à l'utilisateur de choisir une session et afficher les cours disponibles pour cette session.
     * @param scanner Un scanner pour lire l'entrée de l'utilisateur
     * @return Une liste des cours disponibles pour la session choisie
     * @throws IOException Si une erreur de communication avec le serveur se produit
     * @throws ClassNotFoundException Si la classe des objets reçus n'est pas trouvée
     */
    public ArrayList<Course> menu(Scanner scanner) throws IOException, ClassNotFoundException {
        ArrayList<Course> courses = null;
        System.out.print("Veuillez choisir la session pour laquelle consulter la liste des cours: \n1. Automne \n2. Hiver \n3. Eté \n> Choix: ");
        String[] course= {"Automne", "Hiver", "Ete"};
        courses = displayCoursesForSession(course, scanner);
        int choice;
        // On boucle tant que l'utilisateur ne souhaite pas ajouter de cours
        while (true) {
            System.out.println("Choix: \n1. Choisir une session \n2. Ajouter un cours \n> Choix: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Veuillez choisir la session pour laquelle consulter la liste des cours: \n1. Automne \n2. Hiver \n3. Eté \n> Choix: ");
                connect();
                courses = displayCoursesForSession(course, scanner);
            } else if (choice == 2) {
                break;
            } else {
                System.out.println("Choix invalide, veuillez réessayer.");
            }
        }
        return courses;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client("localhost", 1337);
        try {
            client.connect();

            // Charge les cours pour une session donnée
            Scanner scanner = new Scanner(System.in);
            System.out.println("***Bienvenue au portail d'inscription de l'UDEM***");
            ArrayList<Course> courses = client.menu(scanner);

            // Inscription à un cours
            System.out.print("Entrez votre prénom : ");
            String prenom = scanner.nextLine();
            System.out.print("Entrez votre nom : ");
            String nom = scanner.nextLine();
            System.out.print("Entrez votre email : ");
            String email = scanner.nextLine();
            System.out.print("Entrez votre matricule : ");
            String matricule = scanner.nextLine();

            Course selectedCourse = client.searchCours(courses, scanner);

            if (selectedCourse != null) { // On s'assure que le cours existe bien puis envoie une requête au serveur pour enregistrer le cours
                RegistrationForm form = new RegistrationForm(prenom, nom, email, matricule, selectedCourse);
                client.connect();
                String response = client.register(form);
                System.out.println("Félicitation ! Inscription réussie de " + prenom + " au cours " + selectedCourse.getCode());
            } else {
                System.out.println("Le code du cours est invalide.");
            }

            client.disconnect();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

