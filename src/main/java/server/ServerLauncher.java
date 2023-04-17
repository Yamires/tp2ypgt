package server;
/**
 * Classe principale pour lancer le serveur.
 */
public class ServerLauncher {
    /**
     * Le port sur lequel le serveur sera écouté.
     */
    public final static int PORT = 1337;
    /**
     * Point d'entrée pour lancer le serveur.
     * Crée une instance de Server avec le port spécifié et lance le serveur.
     * @param args les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}