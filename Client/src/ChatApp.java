import ChatRemote.ChatRemote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ChatApp extends JFrame {
    JLabel lbHelp, lbCurrentUser;
    JSplitPane jsp;
    JList<String> liste;
    DefaultListModel<String> model;
    JTabbedPane jtb;

    private boolean running = true;

    public ChatApp(ChatRemote r, String clientId) {

        this.setSize(1000, 700);
        this.setLayout(new BorderLayout());
        this.setTitle("ChatApp");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
                try {
                    r.disconnect(clientId);
                } catch (RemoteException ex) {
                    System.out.println("Disconnect error: " + ex.getMessage());
                } finally {
                    dispose();
                    System.exit(0);
                }
            }
        });

        this.setResizable(true);

        // ── North: current user label ─────────────────────────────────
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        northPanel.setBackground(new Color(52, 73, 94));
        lbCurrentUser = new JLabel("Logged in as: " + clientId);
        lbCurrentUser.setFont(new Font("Arial", Font.BOLD, 14));
        lbCurrentUser.setForeground(Color.WHITE);
        northPanel.add(lbCurrentUser);
        this.add(northPanel, BorderLayout.NORTH);

        // ── Right: tabbed pane ────────────────────────────────────────
        jtb = new JTabbedPane();

        // ── Left: client list (ALL clients including yourself) ────────
        model = new DefaultListModel<>();
        loadIds(r); // initial load — returns all clients

        liste = new JList<>(model);
        liste.setFont(new Font("Arial", Font.PLAIN, 13));

        // Highlight yourself in the list with a custom renderer
        liste.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value.equals(clientId)) {
                    label.setText(value + " (you)");
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    if (!isSelected) label.setForeground(new Color(52, 73, 94));
                }
                return label;
            }
        });

        liste.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String id = liste.getSelectedValue();
                    if (id == null || id.equals(clientId)) return; // can't chat with yourself
                    int index = jtb.indexOfTab(id);
                    if (index == -1) {
                        jtb.addTab(id, new FormPanel(r, clientId, id));
                    } else {
                        jtb.setSelectedIndex(index);
                    }

                }
            }
        });

        // ── Center split ──────────────────────────────────────────────
        jsp = new JSplitPane();
        jsp.setLeftComponent(new JScrollPane(liste));
        jsp.setRightComponent(jtb);
        jsp.setDividerLocation(200);
        this.add(jsp, BorderLayout.CENTER);

        // ── South: status bar ─────────────────────────────────────────
        lbHelp = new JLabel(" Ready");
        lbHelp.setFont(new Font("Arial", Font.PLAIN, 12));
        this.add(lbHelp, BorderLayout.SOUTH);

        // ── Polling thread: refresh client list every 3 seconds ───────
        Thread pollThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(3000);
                    ArrayList<String> latestIds = r.getAllIds();

                    SwingUtilities.invokeLater(() -> {
                        model.clear();
                        for (String id : latestIds) {
                            model.addElement(id); // includes yourself
                        }
                        lbHelp.setText(" Online: " + latestIds.size() + " client(s)");
                    });

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (RemoteException ex) {
                    SwingUtilities.invokeLater(() ->
                            lbHelp.setText(" Connection error: " + ex.getMessage())
                    );
                }
            }
        });
        pollThread.setDaemon(true);
        pollThread.start();

        // ── Right-click popup to close a tab ─────────────────────────────
        JPopupMenu tabPopup = new JPopupMenu();
        JMenuItem menuClose = new JMenuItem("Supprimer");

        menuClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = jtb.getSelectedIndex();
                if (index != -1) {
                    jtb.removeTabAt(index);
                }
            }
        });

        tabPopup.add(menuClose);

        jtb.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handlePopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handlePopup(e);
            }

            private void handlePopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    // Find which tab was right-clicked
                    int index = jtb.indexAtLocation(e.getX(), e.getY());
                    if (index != -1) {
                        jtb.setSelectedIndex(index); // select the tab that was right-clicked
                        tabPopup.show(jtb, e.getX(), e.getY());
                    }
                }
            }
        });
    }


    private void loadIds(ChatRemote r) {
        try {
            ArrayList<String> allIds=r.getAllIds();
            for (String id : allIds) {
                model.addElement(id);
            }
        } catch (RemoteException e) {
            System.out.println("Initial load error: " + e.getMessage());
        }
    }
}