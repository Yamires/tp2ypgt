package mvc;

import server.models.Course;
import server.models.RegistrationForm;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;


/**
 * La classe client permet à un utilisateur de se connecter à un serveur pour charger des cours et s'y inscrire.
 */
public class Modele{

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
    public Modele(String hostname, int port) throws IOException, ClassNotFoundException {
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
     * Classe utilitaire pour valider les adresses e-mail.
     */
    public static class EmailValidator {
        private Pattern pattern;
        private Matcher matcher;
        /**
         * Le regex utilisé pour valider les adresses e-mail.
         */
        private static final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        /**
         * Initialise la classe avec le motif regex pour valider les adresses e-mail.
         */
        public EmailValidator() {
            pattern = Pattern.compile(EMAIL_PATTERN);
        }
        /**
         * Valide si une adresse e-mail est valide en utilisant le motif regex spécifié.
         * @param email l'adresse e-mail à valider
         * @return true si l'adresse e-mail est valide, false sinon
         */
        public boolean validate(final String email) {
            matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }


    /**
     * Envoie un formulaire d'inscription au serveur pour s'inscrire à un cours.
     * @param form Le formulaire d'inscription à envoyer
     * @return Une chaîne de caractères indiquant le résultat de l'inscription
     * @throws IOException Si une erreur de communication avec le serveur se produit
     * @throws ClassNotFoundException Si la classe des objets reçus n'est pas trouvée
     */
    public String register(RegistrationForm form) throws IOException, ClassNotFoundException{
        objectOutputStream.writeObject("INSCRIRE");
        objectOutputStream.flush();
        objectOutputStream.writeObject(form);
        objectOutputStream.flush();
        return (String) objectInputStream.readObject();
    }

}


