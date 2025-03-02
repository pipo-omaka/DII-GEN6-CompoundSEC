import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

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
        frame.setLayout(new GridLayout(5, 2)); // เพิ่มบรรทัดสำหรับปุ่มใหม่

        JLabel cardIDLabel = new JLabel("Card ID:");
        cardIDField = new JTextField();
        JLabel pinLabel = new JLabel("PIN:");
        pinField = new JPasswordField();

        managerButton = new JButton("Manager");
        guestButton = new JButton("Guest");

        // เพิ่มปุ่มเพื่อแสดงบันทึกการเปลี่ยนแปลง
        JButton viewLogsButton = new JButton("View Change Logs");
        viewLogsButton.addActionListener(e -> showChangeLogs(cardIDField.getText()));  // ส่ง cardID ที่กรอกมา

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

    // ฟังก์ชันใน AccessControlGUI เพื่อแสดงบันทึกการเปลี่ยนแปลง
    private void showChangeLogs(String cardID) {
        List<String> logs = system.getCardChangesLog(cardID);  // ใช้ List จาก java.util

        if (logs == null || logs.isEmpty()) {  // ตรวจสอบว่า logs ไม่มีค่า
            JOptionPane.showMessageDialog(frame, "No change logs available for this card.", "Change Logs", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder logText = new StringBuilder();
            for (String log : logs) {
                logText.append(log).append("\n");
            }
            JOptionPane.showMessageDialog(frame, logText.toString(), "Change Logs", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ฟังก์ชันยืนยันตัวตน
    private void authenticateUser(String role) {
        String cardID = cardIDField.getText().trim();
        String pin = new String(pinField.getPassword()).trim();

        // เช็คข้อมูลการ์ดและ PIN
        if (cardID.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both Card ID and PIN", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AccessCard card = system.getCard(cardID);
        if (card != null && card.validatePIN(pin)) {
            if (role.equals("Manager")) {
                if (card.getAccessLevels().contains("Manager")) {
                    JOptionPane.showMessageDialog(frame, "Manager Access Granted!");
                    new ManagerPanel(system, card);  // เปิด Manager Panel
                } else {
                    JOptionPane.showMessageDialog(frame, "Access Denied: You are not a Manager!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (role.equals("Guest")) {
                JOptionPane.showMessageDialog(frame, "Guest Access Granted!");
                new GuestPanel(card);  // เปิด Guest Panel
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid Card ID or PIN", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}



