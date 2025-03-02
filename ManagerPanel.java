import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDateTime;


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

            String level = floor + ":" + room;  // สร้าง level ใหม่ที่ต้องการเพิ่ม

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
            String level = floor + ":" + room;

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

    private void checkCardChanges() {
        String cardID = JOptionPane.showInputDialog(frame, "กรุณากรอกรหัสการ์ด:");
        if (cardID == null || cardID.trim().isEmpty()) return;

        List<String> changesLog = system.getCardChangesLog(cardID);  // ดึงข้อมูล log ของการ์ดนี้

        if (changesLog.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "ไม่มีข้อมูลการเปลี่ยนแปลงสำหรับการ์ดนี้", "ข้อมูลการเปลี่ยนแปลง", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder logDetails = new StringBuilder();
            logDetails.append("<html><b>ข้อมูลการเปลี่ยนแปลง:</b><br>");
            for (String log : changesLog) {
                logDetails.append(log).append("<br>");
            }
            logDetails.append("</html>");

            JOptionPane.showMessageDialog(frame, logDetails.toString(), "ข้อมูลการเปลี่ยนแปลง", JOptionPane.INFORMATION_MESSAGE);
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
            String newLevel = floor + " - " + room; // สร้างรูปแบบใหม่

            // ลบสิทธิ์เก่าออก (ถ้ามี)
            List<String> accessLevels = card.getAccessLevels();
            accessLevels.clear();  // ลบข้อมูลทั้งหมดใน accessLevels

            // เพิ่มข้อมูลใหม่เข้าไป
            accessLevels.add(newLevel);   // เพิ่ม level ใหม่
            card.setAccessLevels(accessLevels);  // อัปเดตการ์ด

            // อัปเดตข้อมูลในระบบ
            this.updateCardInSystem(cardID, card); // อัปเดตข้อมูลกลับไปยังระบบ

            // บันทึกการเปลี่ยนแปลงใน log
            logCardChange(cardID, "MODIFY", "Previous level removed, New level -> " + newLevel, "ADMIN001");
            logEvent("🔄 Access level changed for card: " + cardID + " to " + newLevel);

            JOptionPane.showMessageDialog(frame, "แก้ไขบัตรสำเร็จ!");
            updateCardList();  // รีเฟรชข้อมูลใน UI
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

        // สร้างปุ่มสามปุ่ม
        JButton addAccessButton = new JButton("เพิ่มสิทธิ์");
        JButton revokeAccessButton = new JButton("ลบสิทธิ์");
        JButton modifyAccessButton = new JButton("แก้ไขสิทธิ์");
        JButton checkCardButton = new JButton("เช็คข้อมูลการ์ด");
        JButton checkChangesButton = new JButton("ตรวจสอบการเปลี่ยนแปลง");

        // เพิ่มปุ่มลงใน panel
        panel.add(addAccessButton);
        panel.add(revokeAccessButton);
        panel.add(modifyAccessButton);
        panel.add(checkCardButton);
        panel.add(checkChangesButton);

        // สร้าง JScrollPane สำหรับ scroll
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);  // ใส่ panel ลงใน frame

        // ฟังก์ชันที่เรียกใช้งานเมื่อกดปุ่ม
        addAccessButton.addActionListener(e -> addAccessLevel());  // เพิ่มสิทธิ์
        revokeAccessButton.addActionListener(e -> revokeAccessLevel());
        modifyAccessButton.addActionListener(e -> modifyAccessLevel());  // แก้ไขสิทธิ์
        checkCardButton.addActionListener(e -> checkCardInformation());
        checkChangesButton.addActionListener(e -> checkCardChanges()); // เช็คข้อมูลการ์ด
    }
}



