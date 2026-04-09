import ChatRemote.ChatRemote;
import ChatRemote.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

public class ChatImplementation extends UnicastRemoteObject implements ChatRemote {
    ArrayList<Message> arrayMessages = new ArrayList<>();
    ArrayList<String> arrayIds = new ArrayList<>();

    protected ChatImplementation() throws RemoteException {
        arrayIds.add("Forum");
    }

    @Override
    public ArrayList<Message> getAllMessages() throws RemoteException {
        return arrayMessages;
    }

    @Override
    public void addMessage(Message m) throws RemoteException {
        this.arrayMessages.add(m);
    }

    @Override
    public ArrayList<String> getAllIds() throws RemoteException {
        return arrayIds;
    }

    @Override
    public void addId(String id) throws RemoteException {
        this.arrayIds.add(id);

    }

    @Override
    public void disconnect(String id) throws RemoteException{
        ArrayList<String> tempArray = new ArrayList<>(this.arrayIds);
        this.arrayIds.clear();
        for(String tempId:tempArray){
            if(!Objects.equals(id, tempId)){
                this.arrayIds.add(tempId);
            }
        }
    }
}
