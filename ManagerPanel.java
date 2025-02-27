import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManagerPanel {
    private JFrame frame;
    private AccessControlSystem system;
    private JTable cardTable;
    private DefaultTableModel tableModel;
    private AccessCard card; // เพิ่มตัวแปรสำหรับเก็บข้อมูลของบัตรที่เข้าถึง Manager Panel

    public ManagerPanel(AccessControlSystem system, AccessCard card) {
        // ขอกรอก PIN จากผู้ใช้
        String inputPin = JOptionPane.showInputDialog(null, "กรุณากรอกรหัส PIN:");

        // ตรวจสอบสิทธิ์การเข้าถึง "ManagerPanel"
        boolean isManager = card.getAccessLevels().stream()
                .anyMatch(level -> level.equalsIgnoreCase("Manager"));

        if (!isManager || !card.validatePIN(inputPin)) {
            JOptionPane.showMessageDialog(null, "คุณไม่มีสิทธิ์เข้าถึง Manager Panel!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }


        this.system = system;
        frame = new JFrame("Manager Panel");
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        showManagerOptions();
        frame.setVisible(true);
    }

    // 🔹 ฟังก์ชันอัปเดตตารางข้อมูลบัตร
    private void updateCardList() {
        tableModel.setRowCount(0); // ล้างข้อมูลเก่าก่อน
        for (AccessCard card : system.getAllCards()) {
            tableModel.addRow(new Object[]{
                    card.getCardID(),
                    card.getAccessLevels().toString(),
                    card.getAccessLevelCategory()
            });
        }
    }

    // 🔹 ฟังก์ชันเพิ่มสิทธิ์ให้บัตร
    private void addAccessLevel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField cardIDField = new JTextField();
        panel.add(new JLabel("กรุณากรอก Card ID:"));
        panel.add(cardIDField);

        String[] levels = {"High floor", "Medium floor", "Low floor"};
        JComboBox<String> levelComboBox = new JComboBox<>(levels);
        panel.add(new JLabel("เลือกระดับสิทธิ์:"));
        panel.add(levelComboBox);

        JTextField roomField = new JTextField();
        panel.add(new JLabel("กรุณากรอก Room:"));
        panel.add(roomField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "เพิ่มสิทธิ์", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            System.out.println("[DEBUG] กด OK แล้ว!"); // ตรวจสอบว่ามีการกดปุ่มจริงไหม

            String cardID = cardIDField.getText().trim();
            String level = (String) levelComboBox.getSelectedItem();
            String room = roomField.getText().trim();

            if (cardID.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "กรุณากรอกข้อมูลให้ครบถ้วน", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("[ERROR] กรอกข้อมูลไม่ครบ!");
                return;
            }

            String inputPin = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัส PIN:");
            if (inputPin == null || inputPin.trim().isEmpty()) {
                System.out.println("[ERROR] ไม่ได้กรอกรหัส PIN!");
                return;
            }

            System.out.println("[DEBUG] กำลังเรียกใช้ modifyCard() -> CardID: " + cardID + ", Level: " + level);

            if (system.modifyCard(cardID, "ADD", level)) {
                System.out.println("[SUCCESS] เพิ่มสิทธิ์สำเร็จ!");
                JOptionPane.showMessageDialog(frame, "เพิ่มสิทธิ์สำเร็จ!");
                updateCardList();
            } else {
                System.out.println("[ERROR] ไม่สามารถเพิ่มสิทธิ์ให้ Card ID: " + cardID);
                JOptionPane.showMessageDialog(frame, "Card ID ไม่ถูกต้อง!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("[CANCEL] ผู้ใช้กดยกเลิก!");
        }
    }


    //เพิกถอนการ์ด❌
    private void revokeAccessLevel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField cardIDField = new JTextField();
        panel.add(new JLabel("กรุณากรอก Card ID:"));
        panel.add(cardIDField);

        String[] levels = {"High floor", "Medium floor", "Low floor"};
        JComboBox<String> levelComboBox = new JComboBox<>(levels);
        panel.add(new JLabel("เลือกระดับสิทธิ์ที่จะลบ:"));
        panel.add(levelComboBox);

        JTextField roomField = new JTextField();
        panel.add(new JLabel("กรุณากรอก Room:"));
        panel.add(roomField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "ลบสิทธิ์", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            System.out.println("[DEBUG] กด OK เพื่อลบสิทธิ์");

            String cardID = cardIDField.getText().trim();
            String level = (String) levelComboBox.getSelectedItem();
            String room = roomField.getText().trim();

            if (cardID.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "กรุณากรอกข้อมูลให้ครบถ้วน", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("[ERROR] ข้อมูลไม่ครบ!");
                return;
            }

            String inputPin = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัส PIN:");
            if (inputPin == null || inputPin.trim().isEmpty()) {
                System.out.println("[ERROR] ไม่ได้กรอกรหัส PIN!");
                return;
            }

            System.out.println("[DEBUG] กำลังเรียกใช้ modifyCard() -> CardID: " + cardID + ", ลบ Level: " + level);

            if (system.modifyCard(cardID, "REVOKE", level)) {
                System.out.println("[SUCCESS] ลบสิทธิ์สำเร็จ!");
                JOptionPane.showMessageDialog(frame, "ลบสิทธิ์สำเร็จ!");
                updateCardList();
            } else {
                System.out.println("[ERROR] ไม่สามารถลบสิทธิ์ของ Card ID: " + cardID);
                JOptionPane.showMessageDialog(frame, "Card ID ไม่ถูกต้องหรือไม่มีสิทธิ์นี้!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("[CANCEL] ผู้ใช้กดยกเลิก!");
        }
    }


    private void checkCardInformation() {
        String cardID = JOptionPane.showInputDialog(frame, "กรุณากรอก Card ID:");
        if (cardID == null || cardID.trim().isEmpty()) return;

        String inputPin = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัส PIN:");
        if (inputPin == null || inputPin.trim().isEmpty()) return;

        AccessCard card = system.getCard(cardID);
        if (card != null && card.validatePIN(inputPin)) {
            JOptionPane.showMessageDialog(frame, "ข้อมูลการ์ด:\n" + card.toString(), "ข้อมูลการ์ด", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "ข้อมูลการ์ดไม่ถูกต้อง หรือรหัส PIN ไม่ถูกต้อง", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifyAccessLevel() {
        // สร้าง panel สำหรับกรอกข้อมูล
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // จัดวางข้อมูลในแนวตั้ง

        // Card ID
        JTextField cardIDField = new JTextField();
        cardIDField.setPreferredSize(new Dimension(200, 30));
        panel.add(new JLabel("กรุณากรอก Card ID:"));
        panel.add(cardIDField);

        // เลือกระดับสิทธิ์
        String[] levels = {"สูง", "กลาง", "ต่ำ"};
        JComboBox<String> levelComboBox = new JComboBox<>(levels);
        panel.add(new JLabel("เลือกระดับสิทธิ์ที่ต้องการแก้ไข:"));
        panel.add(levelComboBox);

        // กรอก Room
        JTextField roomField = new JTextField();
        roomField.setPreferredSize(new Dimension(200, 30));
        panel.add(new JLabel("กรุณากรอก Room:"));
        panel.add(roomField);

        // ปุ่ม OK
        JButton okButton = new JButton("OK");
        panel.add(okButton);

        // แสดง dialog ที่มีช่องกรอกข้อมูล
        int option = JOptionPane.showOptionDialog(frame, panel, "แก้ไขสิทธิ์",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);

        // ฟังก์ชันที่ทำงานเมื่อกดปุ่ม OK
        okButton.addActionListener(e -> {
            String cardID = cardIDField.getText().trim();
            String level = (String) levelComboBox.getSelectedItem();
            String room = roomField.getText().trim();

            if (cardID.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "กรุณากรอกข้อมูลให้ครบถ้วน", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // ขอรหัส PIN จากผู้ใช้
                String inputPin = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัส PIN:");
                if (inputPin == null || inputPin.trim().isEmpty()) return;

                // แก้ไขสิทธิ์ให้กับการ์ด
                if (system.modifyCard(cardID, "MODIFY", level)) {
                    JOptionPane.showMessageDialog(frame, "แก้ไขสิทธิ์สำเร็จ!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Card ID ไม่ถูกต้อง!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    private void showManagerOptions() {
        // สร้าง panel สำหรับปุ่ม
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // ตั้งแนวการจัดวางปุ่มเป็นแนวตั้ง

        // สร้างปุ่มสามปุ่ม
        JButton addAccessButton = new JButton("เพิ่มสิทธิ์");
        JButton revokeAccessButton = new JButton("ลบสิทธิ์");
        JButton modifyAccessButton = new JButton("แก้ไขสิทธิ์");
        JButton checkCardButton = new JButton("เช็คข้อมูลการ์ด");

        // เพิ่มปุ่มลงใน panel
        panel.add(addAccessButton);
        panel.add(revokeAccessButton);
        panel.add(modifyAccessButton);
        panel.add(checkCardButton);

        // สร้าง JScrollPane สำหรับ scroll
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);  // ใส่ panel ลงใน frame

        // ฟังก์ชันที่เรียกใช้งานเมื่อกดปุ่ม
        addAccessButton.addActionListener(e -> addAccessLevel());  // เพิ่มสิทธิ์
        revokeAccessButton.addActionListener(e -> revokeAccessLevel());
        modifyAccessButton.addActionListener(e -> modifyAccessLevel());  // แก้ไขสิทธิ์
        checkCardButton.addActionListener(e -> checkCardInformation());  // เช็คข้อมูลการ์ด
    }



}



