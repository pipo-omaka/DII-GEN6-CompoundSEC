import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;



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

    public boolean isCardValidWithinTime(AccessCard card) {
        LocalDateTime currentTime = LocalDateTime.now();
        return !card.getExpiryDate().isBefore(currentTime);
    }

    public boolean canAccess(AccessCard card, String floor, String room) {
        List<String> levels = card.getAccessLevels();
        String requiredLevel = "Floor " + floor + " - Room " + room;
        return levels.contains(requiredLevel) && isCardValidWithinTime(card);
    }



    // 🔹 ฟังก์ชันอัปเดตตารางข้อมูลบัตร
    private void updateCardList() {
        if (this.tableModel == null) {
            System.err.println("[ERROR] Table model ยังไม่ได้ถูกกำหนดค่า");
            return;
        }
        tableModel.setRowCount(0);  // ลบข้อมูลทั้งหมดในตารางก่อนการรีเฟรช
        for (AccessCard card : system.getAllCards()) {
            for (String level : card.getAccessLevels()) { // สมมติว่า accessLevels เป็น String
                tableModel.addRow(new Object[]{
                        card.getCardID(),  // แสดงหมายเลขการ์ด
                        level,  // แสดงข้อมูลของชั้นและห้อง (level)
                        card.getAccessLevelCategory()
                });
            }
        }
    }


    // 🔹 ฟังก์ชันเพิ่มสิทธิ์ให้บัตร
    private void addAccessLevel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField cardIDField = new JTextField();
        panel.add(new JLabel("กรุณากรอก Card ID:"));
        panel.add(cardIDField);

        JTextField floorField = new JTextField();
        panel.add(new JLabel("กรุณากรอกชั้น (Floor):"));
        panel.add(floorField);

        JTextField roomField = new JTextField();
        panel.add(new JLabel("กรุณากรอกห้อง (Room):"));
        panel.add(roomField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "เพิ่มสิทธิ์", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String cardID = cardIDField.getText().trim();
            String floor = floorField.getText().trim();
            String room = roomField.getText().trim();

            // ตรวจสอบข้อมูลที่กรอก
            if (cardID.isEmpty() || floor.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "กรุณากรอกข้อมูลให้ครบถ้วน", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AccessCard card = system.getCard(cardID);  // ดึงข้อมูลการ์ดจากระบบ

            if (card == null) {  // ถ้าการ์ดไม่พบ
                JOptionPane.showMessageDialog(frame, "Card ID ไม่ถูกต้อง!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String level = "Floor " + floor + " - " + "Room " + room;  // สร้าง level ใหม่ที่ต้องการเพิ่ม

            List<String> accessLevels = card.getAccessLevels();

            // ตรวจสอบว่ามีสิทธิ์นี้อยู่แล้วหรือไม่
            if (accessLevels.contains(level)) {
                JOptionPane.showMessageDialog(frame, "สิทธิ์นี้มีอยู่แล้ว", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String adminID = "ADMIN001"; // ใช้ค่า Admin ID นี้โดยตรง

            String newPin = card.getPin();
            LocalDateTime newExpiryDate = card.getExpiryDate();

            // เรียกใช้ modifyCard() เพื่อเพิ่มสิทธิ์ใหม่
            boolean updated = system.modifyCard(cardID, "ADD", level, floor, room, newPin, newExpiryDate, adminID);

            if (updated) {
                JOptionPane.showMessageDialog(frame, "เพิ่มสิทธิ์สำเร็จ!");
                updateCardList();  // รีเฟรชข้อมูลใน UI
            } else {
                JOptionPane.showMessageDialog(frame, "ไม่สามารถเพิ่มสิทธิ์ได้", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    //เพิกถอนการ์ด❌
    private void revokeAccessLevel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField cardIDField = new JTextField();
        panel.add(new JLabel("กรุณากรอก Card ID:"));
        panel.add(cardIDField);

        JTextField floorField = new JTextField();
        panel.add(new JLabel("กรุณากรอกชั้น (Floor):"));
        panel.add(floorField);

        JTextField roomField = new JTextField();
        panel.add(new JLabel("กรุณากรอกห้อง (Room):"));
        panel.add(roomField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "ลบสิทธิ์", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String cardID = cardIDField.getText().trim();
            String floor = floorField.getText().trim();
            String room = roomField.getText().trim();

            if (cardID.isEmpty() || floor.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "กรุณากรอกข้อมูลให้ครบถ้วน", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ดึงข้อมูลการ์ดจากระบบ
            AccessCard card = system.getCard(cardID);
            if (card == null) {
                JOptionPane.showMessageDialog(frame, "ไม่พบการ์ดที่มี ID นี้", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ระบุระดับสิทธิ์ที่ต้องการลบ
            String level = "Floor " + floor + " - " + "Room " + room;

            // เพิ่มการดีบักเพื่อเช็คค่าก่อนการลบ
            System.out.println("Before removal: " + card.getAccessLevels());
            System.out.println("Trying to remove level: " + level);

            // ดึง Access Levels จากการ์ดโดยตรง
            List<String> accessLevels = card.getAccessLevels();
            System.out.println("Access Levels: " + accessLevels);

            // ลบสิทธิ์ที่ตรงกัน (Case Insensitive และ Trim)
            boolean removed = accessLevels.removeIf(l -> l.trim().equalsIgnoreCase(level.trim()));
            System.out.println("After removal: " + accessLevels);
            System.out.println("Removed: " + removed);

            if (!removed) {
                JOptionPane.showMessageDialog(frame, "ไม่พบสิทธิ์นี้ในการ์ด", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // อัปเดต Access Levels ในการ์ด
            card.setAccessLevels(accessLevels);

            // อัปเดตการ์ดในระบบ
            system.updateCardInSystem(cardID, card);
            system.logCardChange(cardID, "REVOKE", level, "ADMIN001");

            // รีเฟรช UI
            updateCardList();

            // แจ้งผล
            JOptionPane.showMessageDialog(frame, "ลบสิทธิ์สำเร็จ!");
        }
    }

    private void checkCardInformation() {
        String cardID = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัสการ์ด:");
        if (cardID == null || cardID.trim().isEmpty()) return;

        String inputPin = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัส PIN:");
        if (inputPin == null || inputPin.trim().isEmpty()) return;

        AccessCard card = system.getCard(cardID);
        if (card != null && card.validatePIN(inputPin)) {
            // แสดงข้อมูลการ์ดโดยจัดให้แต่ละข้อมูลอยู่บนบรรทัดแยกกัน
            String cardInfo = "<html>";
            cardInfo += "ข้อมูลการ์ด:\n";
            cardInfo += "Card ID: " + card.getCardID() + "<br>";
            cardInfo += "สิทธิ์การเข้าถึง: " + String.join(", ", card.getAccessLevels()) + "<br>";
            cardInfo += "วันที่หมดอายุ: " + card.getExpiryDate() + "<br>";
            cardInfo += "</html>";

            JOptionPane.showMessageDialog(frame, cardInfo, "ข้อมูลการ์ด", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "ข้อมูลการ์ดไม่ถูกต้อง หรือรหัส PIN ไม่ถูกต้อง", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ฟังก์ชันตรวจสอบการเปลี่ยนแปลงของการ์ด
    private void checkCardChanges() {
        // Example to show changes for selected card.
        String cardID = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัสการ์ดที่ต้องการตรวจสอบการเปลี่ยนแปลง:");
        if (cardID != null) {
            List<String> logs = system.getCardChangesLog(cardID);
            if (logs != null && !logs.isEmpty()) {
                StringBuilder logText = new StringBuilder();
                for (String log : logs) {
                    logText.append(log).append("\n");
                }
                JOptionPane.showMessageDialog(frame, logText.toString(), "Change Logs", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "ไม่มีการเปลี่ยนแปลงที่บันทึกสำหรับการ์ดนี้", "Change Logs", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    private void modifyAccessLevel() {
        // ขอ Card ID จากผู้ใช้
        String cardID = JOptionPane.showInputDialog(frame, "กรุณากรอก Card ID:");
        if (cardID == null || cardID.trim().isEmpty()) return;

        // ค้นหาการ์ด
        AccessCard card = system.getCard(cardID);
        if (card == null) {
            JOptionPane.showMessageDialog(frame, "ไม่พบการ์ดนี้ในระบบ!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ขอรหัส PIN
        String inputPin = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัส PIN:");
        if (inputPin == null || inputPin.trim().isEmpty() || !card.validatePIN(inputPin)) {
            JOptionPane.showMessageDialog(frame, "รหัส PIN ไม่ถูกต้อง!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // GUI สำหรับแก้ไขข้อมูล
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // แสดงข้อมูลปัจจุบัน
        panel.add(new JLabel("สิทธิ์ปัจจุบัน: " + String.join(", ", card.getAccessLevels())));

        // ใช้ JComboBox สำหรับเลือกระดับสิทธิ์ (Low, Medium, High)
        JComboBox<String> levelDropdown = new JComboBox<>(new String[]{"Low Floor", "Medium Floor", "High Floor"});
        panel.add(new JLabel("เลือกระดับสิทธิ์ใหม่:"));
        panel.add(levelDropdown);

        JTextField floorField = new JTextField();
        panel.add(new JLabel("ชั้นใหม่:"));
        panel.add(floorField);

        JTextField roomField = new JTextField();
        panel.add(new JLabel("ห้องใหม่:"));
        panel.add(roomField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "แก้ไขสิทธิ์", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String level = (String) levelDropdown.getSelectedItem();
            String floor = floorField.getText().trim();
            String room = roomField.getText().trim();

            // แปลงข้อมูล level, floor, room เป็น newLevel ที่ใช้ใน accessLevels
            String newLevel = "Floor " + floor + " - " + "Room " + room; // สร้างรูปแบบใหม่

            // ลบสิทธิ์เก่าออก (ถ้ามี)
            List<String> accessLevels = card.getAccessLevels();
            accessLevels.clear();  // ลบข้อมูลทั้งหมดใน accessLevels

            // เพิ่มข้อมูลใหม่เข้าไป
            accessLevels.add(newLevel);   // เพิ่ม level ใหม่
            card.setAccessLevels(accessLevels);  // อัปเดตการ์ด

            // อัปเดตข้อมูลในระบบ
            system.updateCardInSystem(cardID, card); // อัปเดตข้อมูลกลับไปยังระบบ

            // บันทึกการเปลี่ยนแปลงใน log
            system.logCardChange(cardID, "MODIFY", "Previous level removed, New level -> " + newLevel, "ADMIN001");
            system.logEvent("🔄 Access level changed for card: " + cardID + " to " + newLevel);

            JOptionPane.showMessageDialog(frame, "แก้ไขบัตรสำเร็จ!");
            updateCardList();  // รีเฟรชข้อมูลใน UI
            // เพิ่มการบันทึกการเปลี่ยนแปลงในระบบ
            system.logAuditTrail("MODIFY", cardID, "Changed access level to " + newLevel, "ADMIN001");

        }
    }


    private void showChangeLogs(String cardID) {
        List<String> logs = system.getCardChangesLog(cardID);
        if (logs == null || logs.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No change logs found for this card.", "Change Logs", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder logText = new StringBuilder();
        for (String log : logs) {
            logText.append(log).append("\n");
        }
        JOptionPane.showMessageDialog(frame, logText.toString(), "Change Logs", JOptionPane.INFORMATION_MESSAGE);
    }


    private void showManagerOptions() {
        // สร้าง panel สำหรับปุ่ม
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // ตั้งแนวการจัดวางปุ่มเป็นแนวตั้ง

        // สร้างปุ่ม
        JButton addCardButton = new JButton("เพิ่มการ์ด");
        JButton deleteCardButton = new JButton("ลบการ์ด");
        JButton addAccessButton = new JButton("เพิ่มสิทธิ์");
        JButton revokeAccessButton = new JButton("ลบสิทธิ์");
        JButton modifyAccessButton = new JButton("แก้ไขสิทธิ์");
        JButton checkCardButton = new JButton("เช็คข้อมูลการ์ด");
        JButton checkChangesButton = new JButton("ตรวจสอบการเปลี่ยนแปลง");

        // เพิ่ม Action Listener ให้กับปุ่ม
        addCardButton.addActionListener(e -> addCard());
        deleteCardButton.addActionListener(e -> deleteCard());
        addAccessButton.addActionListener(e -> addAccessLevel());
        revokeAccessButton.addActionListener(e -> revokeAccessLevel());
        modifyAccessButton.addActionListener(e -> modifyAccessLevel());
        checkCardButton.addActionListener(e -> checkCardInformation());
        checkChangesButton.addActionListener(e -> checkCardChanges());

        // เพิ่มปุ่มลงใน panel
        panel.add(addCardButton);
        panel.add(deleteCardButton);
        panel.add(addAccessButton);
        panel.add(revokeAccessButton);
        panel.add(modifyAccessButton);
        panel.add(checkCardButton);
        panel.add(checkChangesButton);

        // สร้าง JScrollPane สำหรับ scroll
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);  // ใส่ panel ลงใน frame
    }


    private void addCard() {
        // สร้าง panel สำหรับกรอกข้อมูล
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // ตั้งแนวการจัดวางปุ่มเป็นแนวตั้ง

        // ข้อมูลการ์ด
        JTextField cardIdField = new JTextField();
        panel.add(new JLabel("กรุณากรอกรหัสการ์ด:"));
        panel.add(cardIdField);

        // PIN
        JTextField pinField = new JTextField();
        panel.add(new JLabel("กรุณากรอกรหัส PIN:"));
        panel.add(pinField);

        // ชั้น
        JTextField floorField = new JTextField();
        panel.add(new JLabel("กรุณากรอกชั้น (Floor):"));
        panel.add(floorField);

        // ห้อง
        JTextField roomField = new JTextField();
        panel.add(new JLabel("กรุณากรอกห้อง (Room):"));
        panel.add(roomField);

        // ระดับการเข้าถึง (Access Level)
        String[] accessLevelsUI = {"Low Floor", "Medium Floor", "High Floor"};
        JComboBox<String> accessLevelCombo = new JComboBox<>(accessLevelsUI);
        panel.add(new JLabel("กรุณาเลือกระดับการเข้าถึง:"));
        panel.add(accessLevelCombo);

        // วันหมดอายุ
        JTextField expiryField = new JTextField();
        panel.add(new JLabel("กรุณากรอกวันหมดอายุ (yyyy-mm-dd):"));
        panel.add(expiryField);

        // แสดงกรอบ UI
        int result = JOptionPane.showConfirmDialog(frame, panel, "เพิ่มการ์ดใหม่", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // รับค่าจากฟอร์ม
            String cardId = cardIdField.getText().trim();
            String pin = pinField.getText().trim();
            String floor = floorField.getText().trim();
            String room = roomField.getText().trim();
            String accessLevel = (String) accessLevelCombo.getSelectedItem();  // รับค่าระดับการเข้าถึง
            String expiryDateStr = expiryField.getText().trim();

            // ตรวจสอบข้อมูลที่กรอก
            if (cardId.isEmpty() || pin.isEmpty() || floor.isEmpty() || room.isEmpty() || accessLevel.isEmpty() || expiryDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "กรุณากรอกข้อมูลให้ครบถ้วน", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // แปลงวันที่หมดอายุจาก String เป็น LocalDateTime
            LocalDateTime expiryDate;
            try {
                expiryDate = LocalDateTime.parse(expiryDateStr + "T00:00:00");  // แปลง string เป็น LocalDateTime
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "รูปแบบวันที่ไม่ถูกต้อง!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // สร้างการ์ดใหม่ในระบบ
            String resultMessage = CardManagement.addCard(cardId, pin, floor, room, expiryDate);
            JOptionPane.showMessageDialog(frame, resultMessage);

            // เพิ่มการตั้งค่าการเข้าถึงใหม่ให้กับการ์ดที่เพิ่ม
            AccessCard newCard = system.getCard(cardId);  // ดึงการ์ดที่เพิ่มจากระบบ
            if (newCard != null) {
                String newLevel = "Floor " + floor + " - Room " + room;
                List<String> accessLevels = newCard.getAccessLevels();  // ดึง access levels ที่มีอยู่
                if (accessLevels == null) {
                    accessLevels = new ArrayList<>();  // หากไม่พบ accessLevels ให้สร้างใหม่
                }
                accessLevels.add(newLevel);  // เพิ่ม level ใหม่
                newCard.setAccessLevels(accessLevels);  // อัปเดตการ์ด
                system.updateCardInSystem(cardId, newCard);  // อัปเดตการ์ดในระบบ
            }

            // บันทึกการเปลี่ยนแปลง
            system.logCardChange(cardId, "ADD", "Card Added", "ADMIN001");

            // ให้สามารถตรวจสอบการเปลี่ยนแปลงการ์ดที่เพิ่มใหม่ได้
            SwingUtilities.invokeLater(() -> {
                updateCardList();  // รีเฟรชตารางการ์ด
                JOptionPane.showMessageDialog(frame, "บัตรถูกเพิ่มเรียบร้อยแล้ว! คุณสามารถตรวจสอบข้อมูลการ์ดอื่นๆ ได้ทันที");
            });
        }
    }

    private void deleteCard() {
        String cardId = JOptionPane.showInputDialog("กรุณากรอกรหัสการ์ดที่ต้องการลบ:");
        if (cardId != null) {
            String result = CardManagement.removeCard(cardId);
            JOptionPane.showMessageDialog(frame, result);
        }
    }

}



