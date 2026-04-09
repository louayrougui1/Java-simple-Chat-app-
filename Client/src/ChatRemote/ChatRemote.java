package ChatRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatRemote extends Remote {
    public ArrayList<Message> getAllMessages() throws RemoteException;

    public void addMessage(Message m) throws RemoteException;

    public void disconnect(String id) throws RemoteException;

    public ArrayList<String> getAllIds() throws RemoteException;

    public void addId(String id) throws RemoteException;

}
