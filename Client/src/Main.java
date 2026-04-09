import ChatRemote.ChatRemote;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Objects;

import ChatRemote.Message;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Client...");
        String url = "rmi://localhost:9003/chat";
        try {
            ChatRemote r = (ChatRemote) Naming.lookup(url);
            String id = JOptionPane.showInputDialog("Donner votre id");
            while (Objects.equals(id, "Forum")){
                id = JOptionPane.showInputDialog("Donner votre id (Different de Forum)");
            }
            r.addId(id);
            ChatApp app = new ChatApp(r, id);
            app.setVisible(true);
            /*
            new Thread(new Runnable() {
                @Override
                public void run() {

                        while (true) {
                        String messageBody = JOptionPane.showInputDialog("Taper un message");
                        Date sendingDate = new Date();
                        Message newMessage = new Message(id, messageBody, sendingDate);
                        try {
                            r.addMessage(newMessage);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
            */

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println("-------------------------------");
                        try {
                            System.out.println(r.getAllMessages());
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();*/


        } catch (NotBoundException e) {
            System.out.println("Not bounded on server: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("Erreur URL: " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("Erreur lors de l'invocation: " + e.getMessage());
        }

    }
}