import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CardManagement {
    private static Map<String, AccessCard> cards = new HashMap<>(); // เก็บข้อมูลการ์ดทั้งหมดในระบบ

    // ฟังก์ชันเพิ่มการ์ด
    public static String addCard(String cardId, String pin, String floor, String room, LocalDateTime expiryDate) {
        if (cards.containsKey(cardId)) {
            return "การ์ด " + cardId + " มีอยู่แล้วในระบบ";
        }

        // สร้างการ์ดใหม่ (ใช้ StandardAccessCard)
        StandardAccessCard newCard = new StandardAccessCard(cardId, pin, floor, room, expiryDate);
        cards.put(cardId, newCard);  // เก็บการ์ดในระบบ
        return "เพิ่มการ์ด " + cardId + " สำเร็จ";
    }


    // ฟังก์ชันลบการ์ด
    public static String removeCard(String cardId) {
        if (cards.containsKey(cardId)) {
            cards.remove(cardId);
            return "ลบการ์ด " + cardId + " สำเร็จ";
        }
        return "ไม่พบการ์ด " + cardId;
    }


    // ฟังก์ชันแสดงรายการการ์ดทั้งหมด
    public void displayCards() {
        System.out.println("รายการการ์ด:");
        for (Map.Entry<String, AccessCard> entry : cards.entrySet()) {
            AccessCard card = entry.getValue();

            if (card instanceof StandardAccessCard) {
                StandardAccessCard standardCard = (StandardAccessCard) card;
                System.out.println("การ์ด: " + entry.getKey() + " | PIN: " + card.getPin() + " | ชั้น: " + standardCard.getFloor() + " | ห้อง: " + standardCard.getRoom() + " | วันหมดอายุ: " + card.getExpiryDate());
            } else {
                System.out.println("การ์ด: " + entry.getKey() + " | PIN: " + card.getPin() + " | วันหมดอายุ: " + card.getExpiryDate());
            }
        }
    }





    // ฟังก์ชันเพื่อเพิ่มการ์ดใหม่จาก UI
    private void addCardFromUI(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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
        String[] accessLevels = {"ระดับ 1 - พื้นฐาน", "ระดับ 2 - พิเศษ", "ระดับ 3 - ผู้ดูแลระบบ"};
        JComboBox<String> accessLevelCombo = new JComboBox<>(accessLevels);
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
                expiryDate = LocalDateTime.parse(expiryDateStr + "T00:00:00");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "รูปแบบวันที่ไม่ถูกต้อง!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // สร้างการ์ดใหม่ในระบบ
            String resultMessage = CardManagement.addCard(cardId, pin, floor, room, expiryDate);
            JOptionPane.showMessageDialog(frame, resultMessage);
        }
    }
}


