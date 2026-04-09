import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Server...");
        try {
            LocateRegistry.createRegistry(9003);
            String url = "rmi://localhost:9003/chat";
            ChatImplementation chatImp = new ChatImplementation();
            Naming.rebind(url, chatImp);
        } catch (RemoteException e) {
            System.out.println("Erreur Registery: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("Erreur Dans l'URL: " + e.getMessage());
        }
    }
}