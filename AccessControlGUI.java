import javax.swing.*;
import java.awt.*;

public class AccessControlGUI {
    private JFrame frame;
    private JTextField cardIDField;
    private JPasswordField pinField;
    private JButton managerButton, guestButton;
    private AccessControlSystem system;

    public AccessControlGUI(AccessControlSystem system) {
        this.system = system;

        frame = new JFrame("Access Control System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(4, 2));

        JLabel cardIDLabel = new JLabel("Card ID:");
        cardIDField = new JTextField();
        JLabel pinLabel = new JLabel("PIN:");
        pinField = new JPasswordField();

        managerButton = new JButton("Manager");
        guestButton = new JButton("Guest");

        managerButton.addActionListener(e -> authenticateUser("Manager"));
        guestButton.addActionListener(e -> authenticateUser("Guest"));

        frame.add(cardIDLabel);
        frame.add(cardIDField);
        frame.add(pinLabel);
        frame.add(pinField);
        frame.add(managerButton);
        frame.add(guestButton);

        frame.setVisible(true);
    }

    private void authenticateUser(String role) {
        String cardID = cardIDField.getText();
        String pin = new String(pinField.getPassword());

        AccessCard card = system.getCard(cardID);
        if (card != null && card.validatePIN(pin)) {
            if (role.equals("Manager")) {
                // ตรวจสอบว่าบัตรนั้นมี Access Levels เป็น "Manager"
                if (card.getAccessLevels().contains("Manager")) {
                    JOptionPane.showMessageDialog(frame, "Manager Access Granted!");
                    new ManagerPanel(system, card);  // เปิด Manager Panel
                } else {
                    JOptionPane.showMessageDialog(frame, "Access Denied: You are not a Manager!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (role.equals("Guest")) {
                // เปิด Guest Panel
                JOptionPane.showMessageDialog(frame, "Guest Access Granted!");
                new GuestPanel(card);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid Card ID or PIN", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}





