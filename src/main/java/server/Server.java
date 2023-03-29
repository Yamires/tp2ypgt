package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe représentant le serveur qui traite les demandes de connexion des clients pour
 * enregistrer les inscriptions aux cours et charger les informations sur les cours.
 */
public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Constructeur de la classe Server.
     * @param port Le numéro de port sur lequel le serveur écoutera les connexions entrantes.
     * @throws IOException Si une erreur se produit lors de la création du ServerSocket.
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajoute un gestionnaire d'événements au serveur.
     * @param h Le gestionnaire d'événements à ajouter.
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Notifie les gestionnaires d'événements de l'arrivée d'une commande et de son argument.
     * @param cmd Le nom de la commande reçue.
     * @param arg L'argument de la commande.
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Lance le serveur pour accepter les connexions entrantes et traiter les commandes des utilisateurs
     * Déconnecte le client une fois le flux déterminé
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * La méthode écoute les commandes envoyées par l'utilisateur et les traite.
     * @throws IOException Si une erreur se produit lors de la lecture du flux d'entrée.
     * @throws ClassNotFoundException Si une erreur se produit lors de la conversion d'un objet lu.
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Traite la ligne de commande reçue et la sépare en commande et argument.
     * @param line La ligne de commande à traiter.
     * @return Un objet Pair contenant la commande et l'argument.
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Déconnecte le client du serveur et ferme le stream de cmd.
     * @throws IOException Si une erreur se produit lors de la fermeture des flux ou du socket.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Gère les événements en fonction de la commande reçue.
     * @param cmd La commande reçue du client.
     * @param arg L'argument de la commande.
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     @throws Exception si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux
     */
    public void handleLoadCourses(String arg) {
        // Chemin du fichier cours.txt
        String filePath = "server/data/cours.txt";
        // Liste des cours pour stocker les objets Course filtrés par session
        ArrayList<Course> courses = new ArrayList<>();

        try {
            // Lit le fichier cours.txt
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            // Lit chaque ligne du fichier
            while ((line = reader.readLine()) != null) {
                // Sépare les champs sur les tabulations
                String[] fields = line.split("\t");
                Course course = new Course(fields[1], fields[0], fields[2]);

                // Si la session du cours correspond à l'argument, ajoute le cours à la liste
                if (course.getSession().equalsIgnoreCase(arg)) {
                    courses.add(course);
                }
            }

            // Ferme le fichier
            reader.close();

            // Envoie la liste des cours filtrés au client
            objectOutputStream.writeObject(courses);
            objectOutputStream.flush();
        } catch (FileNotFoundException e) {
            // Gère les erreurs liées à l'ouverture du fichier
            System.err.println("Fichier non trouvé: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            // Gère les erreurs liées à la lecture/écriture
            System.err.println("Erreur lors de la lecture/écriture du fichier: " + filePath);
            e.printStackTrace();
        }
    }



    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     @throws Exception si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        // Chemin du fichier registrations.txt
        String filePath = "server/data/inscription.txt";

        try {
            // Récupérer l'objet RegistrationForm envoyé par le client
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();

            // Ouvrir le fichier registrations.txt en mode append
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

            // Écrire les informations de l'inscription dans le fichier
            String registrationInfo = registrationForm.getPrenom() + "\t" + registrationForm.getNom() + "\t" +
                    registrationForm.getEmail() + "\t" + registrationForm.getMatricule() + "\t" +
                    registrationForm.getCourse().getCode() + "\t" + registrationForm.getCourse().getName() + "\t" +
                    registrationForm.getCourse().getSession() + "\n";

            writer.write(registrationInfo);

            // Fermer le fichier
            writer.close();

            // Envoyer un message de confirmation au client
            objectOutputStream.writeObject("Inscription enregistrée avec succès.");
            objectOutputStream.flush();
        } catch (IOException e) {
            // Gérer les erreurs liées à la lecture/écriture
            System.err.println("Erreur lors de la lecture/écriture du fichier: " + filePath);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // Gérer les erreurs liées à la conversion de l'objet
            System.err.println("Erreur lors de la conversion de l'objet RegistrationForm.");
            e.printStackTrace();
        }
    }

}

