import ChatRemote.ChatRemote;
import ChatRemote.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FormPanel extends JPanel {
    String id;

    JTextArea messageArea;
    JTextField inputField;
    JButton btn_send, btn_close;
    JPanel north_panel, center_panel, south_panel, input_panel;
    JScrollPane scrollPane;
    JLabel lb_title;
    int nbMessages = 0;
    ArrayList<Message> allMessages;
    ChatRemote r;
    String destinationId;

    public FormPanel(ChatRemote r, String clientId, String destinationId) {
        this.r = r;
        this.id = clientId;
        this.destinationId = destinationId;
        this.setLayout(new BorderLayout(5, 5));
        //get all messages
        try {
            allMessages = r.getAllMessages();
            nbMessages = countPrivateMessages(allMessages, "main");
            updateTextArea();

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("-------------------------------");
                    try {
                        ArrayList<Message> allMessagesChecker = r.getAllMessages();
                        int newNbMessages = countPrivateMessages(allMessagesChecker, "Checker");
                        if (newNbMessages > nbMessages) {
                            allMessages = new ArrayList<>(allMessagesChecker);
                            nbMessages = newNbMessages;
                            updateTextArea();
                        }
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
        }).start();
        // ── North: title bar ──────────────────────────────────────────
        north_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lb_title = new JLabel("Chat - " + destinationId);
        lb_title.setFont(new Font("Arial", Font.BOLD, 14));
        north_panel.add(lb_title);
        this.add(north_panel, BorderLayout.NORTH);

        // ── Center: scrollable message display area ───────────────────
        messageArea = new JTextArea();
        messageArea.setEditable(false);          // read-only display
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 13));
        messageArea.setMargin(new Insets(5, 5, 5, 5));

        scrollPane = new JScrollPane(messageArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, BorderLayout.CENTER);

        // ── South: input field + buttons ──────────────────────────────
        south_panel = new JPanel(new BorderLayout(5, 5));

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 13));

        btn_send = new JButton("Send");

        // Send on button click
        btn_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Send on Enter key press inside the input field
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });


        // Button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.add(btn_send);

        south_panel.add(inputField, BorderLayout.CENTER);
        south_panel.add(buttonPanel, BorderLayout.EAST);
        south_panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        this.add(south_panel, BorderLayout.SOUTH);
    }

    // ── Helper: append a message and auto-scroll to bottom ────────────

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            messageArea.append("You: " + text + "\n");
            Date sendingDate = new Date();
            Message newMessage = new Message(id, text, sendingDate, destinationId);
            try {
                r.addMessage(newMessage);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            inputField.setText("");
            // Auto-scroll to the latest message
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        }
    }

    private void updateTextArea() {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText(""); // clear first
            for (Message m : allMessages) {
                if (Objects.equals(m.getDestinationId(), "Forum") && Objects.equals(this.destinationId, "Forum")) {
                    if (Objects.equals(m.getIdSender(), this.id)) {
                        messageArea.append("You: " + m.getMessageBody() + "\n");
                    } else {
                        messageArea.append("Sender: '" + m.getIdSender() + "' : " + m.getMessageBody() + "\n");
                    }
                } else if (Objects.equals(m.getDestinationId(), this.id) && Objects.equals(this.destinationId, m.getIdSender())) {
                    messageArea.append("Sender: '" + m.getIdSender() + "' : " + m.getMessageBody() + "\n");
                } else if (Objects.equals(m.getDestinationId(), this.destinationId) && Objects.equals(this.id, m.getIdSender())) {
                    messageArea.append("You: " + m.getMessageBody() + "\n");
                }
            }
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    private int countPrivateMessages(ArrayList<Message> list, String ch) {
        int n = 0;
        for (Message m : list) {
            if (Objects.equals(m.getDestinationId(), "Forum") && Objects.equals(this.destinationId, "Forum")) {
                n++;
            } else if (Objects.equals(m.getDestinationId(), this.id) && Objects.equals(this.destinationId, m.getIdSender())) {
                n++;
            }
        }
        System.out.println(ch + " n= " + n);
        return n;
    }
}