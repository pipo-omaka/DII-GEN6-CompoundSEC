import java.time.LocalDateTime;
import java.util.*;
import java.util.List;  // ใช้ java.util.List สำหรับเก็บข้อมูล
import java.util.ArrayList;


//====== Base Class ======
abstract class AccessCard {
    private String cardID;
    private String pin;
    private List<String> accessLevels;
    private List<String> facadeIDs;
    private LocalDateTime expiryDate;
    private String accessLevelCategory;



    public AccessCard(String cardID, String pin, LocalDateTime expiryDate) {
        this.cardID = cardID;
        this.pin = pin;
        this.expiryDate = expiryDate;
        this.accessLevels = new ArrayList<>();
        this.facadeIDs = new ArrayList<>();
    }

    public void setAccessLevels(List<String> accessLevels) {
        this.accessLevels = accessLevels;
    }

    public boolean validatePIN(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public abstract void encryptCardData();

    public String getCardID() {
        return cardID;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String newPin) {
        this.pin = newPin;
    }

    public void setExpiryDate(LocalDateTime newExpiryDate) {
        this.expiryDate = newExpiryDate;
    }


    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

    public List<String> getAccessLevels() {
        return accessLevels;
    }

    public String getAccessLevelCategory() {
        return this.accessLevelCategory;
    }

    public void clearAccessLevels() {
        this.accessLevels.clear();
    }

    public void addAccessLevel(String level) {
        if (!this.accessLevels.contains(level)) { // เพิ่มเงื่อนไขไม่ให้มีระดับสิทธิ์ซ้ำ
            this.accessLevels.add(level);
        }
    }

    public void removeAccessLevel(String level) {
        if (accessLevels != null && accessLevels.contains(level)) {
            accessLevels.remove(level);  // ลบสิทธิ์ที่ตรงกับที่ระบุ
        }
    }

    public void addFacadeID(String facadeID) {
        if (!facadeIDs.contains(facadeID)) {
            facadeIDs.add(facadeID);
        }
    }

    public List<String> getFacadeIDs() {
        return facadeIDs;
    }

    public boolean hasAccess(String level, String inputPin, String facadeID) {
        if (this instanceof TimeBasedAccessCard) {
            // เข้ารหัสข้อมูลการ์ดตามเวลา
            encryptCardData();  // ทำการเข้ารหัส
            System.out.println("Encrypted Card Data: " + ((TimeBasedAccessCard) this).getEncryptedData());
        }

        // ตรวจสอบ PIN และสิทธิ์การเข้าถึงตามระดับและ Facade ID
        return validatePIN(inputPin) && accessLevels.contains(level) && facadeIDs.contains(facadeID) && LocalDateTime.now().isBefore(expiryDate);
    }




    @Override
    public String toString() {
        return "CardID: " + cardID + ", Levels: " + accessLevels.toString() + ", Expiry: " + expiryDate;
    }
}

//====== Decorator for Temporary Permission ======

class TemporaryPermissionDecorator extends AccessCard {
    private AccessCard decoratedCard;
    private String tempPermission;
    private LocalDateTime expiry;

    public TemporaryPermissionDecorator(AccessCard card, String tempPermission, LocalDateTime expiry) {
        super(card.getCardID(), "****", card.getExpiryDate()); // ใช้ "****" แทน PIN เพื่อป้องกันข้อมูลหลุด
        this.decoratedCard = card;
        this.tempPermission = tempPermission;
        this.expiry = expiry;
    }



    @Override
    public boolean hasAccess(String level, String inputPin, String facadeID) {
        if (LocalDateTime.now().isBefore(expiry) && level.equals(tempPermission)) {
            return true;
        }
        return decoratedCard.hasAccess(level, inputPin, facadeID);
    }


    @Override
    public void encryptCardData() {
        decoratedCard.encryptCardData();
    }
}

//====== Subclass for Time-Based Encrypted Access Cards ======
class TimeBasedAccessCard extends AccessCard {
    private String encryptedData;

    public TimeBasedAccessCard(String cardID, String pin, LocalDateTime expiryDate) {
        super(cardID, pin, expiryDate);
    }

    @Override
    public void encryptCardData() {
        // การเข้ารหัสที่สามารถเปลี่ยนแปลงได้ตามเวลา
        this.encryptedData = "Encrypted:" + getCardID() + "@" + getExpiryDate().toString();
    }

    public String getEncryptedData() {
        return encryptedData;
    }
}


//====== Strategy Pattern for Authentication ======❗️ใช้ตรวจสอบสิทธิ์นโยบายของบัตร
interface AccessVerificationStrategy {
    boolean verify(AccessCard card, String level, String inputPin);
}

// Concrete Strategy 1: Basic Authentication (ตรวจเฉพาะหมายเลขบัตร)
class BasicAuthentication implements AccessVerificationStrategy {
    @Override
    public boolean verify(AccessCard card, String level, String inputPin) {
        return card.hasAccess(level, inputPin, "DEFAULT_FACADE_ID");
    }
}




//====== Access Control System ======
class AccessControlSystem {
    private Map<String, AccessCard> registeredCards;
    private List<String> auditLogs;
    private List<AccessCard> cards;
    private AuditLog auditLog;
    private Map<String, List<String>> changesLogMap;


    public AccessControlSystem() {
        registeredCards = new HashMap<>();
        auditLogs = new ArrayList<>();
        changesLogMap = new HashMap<>();
        this.cards = new ArrayList<>();
        this.auditLog = new AuditLog();
    }

    public void registerCard(AccessCard card) {
        registeredCards.put(card.getCardID(), card);
        logEvent("Card Registered: " + card.getCardID());
    }

    public AccessCard getCard(String cardID) {
        return registeredCards.get(cardID);
    }

    public Collection<AccessCard> getAllCards() {
        return registeredCards.values();
    }

    public List<String> getCardChangesLog(String cardID) {
        // ดึงข้อมูลจาก changesLogMap หากไม่มีข้อมูลให้คืนค่าเป็น ArrayList ที่ว่างเปล่า
        List<String> logs = changesLogMap.getOrDefault(cardID, new ArrayList<>());

        // ตรวจสอบว่ามี Log หรือไม่
        if (logs.isEmpty()) {
            System.out.println("DEBUG: ไม่มีการเปลี่ยนแปลงสำหรับ Card ID: " + cardID);
        } else {
            System.out.println("DEBUG: Log ที่พบสำหรับ Card ID " + cardID + " -> " + logs);
        }

        return logs;
    }

    public void updateCardInSystem(String cardID, AccessCard card) {
        registeredCards.put(cardID, card);
        System.out.println("Updated card in system: " + cardID);
        System.out.println("Current Access Levels: " + registeredCards.get(cardID).getAccessLevels()); // เช็คค่าหลังอัปเดต
    }



    public boolean modifyCard(String cardID, String action, String level, String floor, String room, String newPin, LocalDateTime newExpiryDate, String adminID) {
        AccessCard card = getCard(cardID); // ✅ ดึงข้อมูลการ์ดจากระบบใหม่เสมอ

        if (card == null) {
            System.out.println("❌ ERROR: ไม่พบการ์ด " + cardID);
            return false; // ถ้าไม่พบการ์ด
        }

        List<String> accessLevels = new ArrayList<>(card.getAccessLevels());  // ✅ ทำสำเนาเพื่อป้องกันปัญหา
        boolean updated = false;

        // 🟢 การเพิ่มสิทธิ์
        if ("ADD".equalsIgnoreCase(action)) {
            if (!accessLevels.contains(level)) {
                accessLevels.add(level);  // ✅ เพิ่มสิทธิ์
                card.setAccessLevels(accessLevels);  // ✅ ตั้งค่าใหม่
                this.updateCardInSystem(cardID, card); // ✅ อัปเดตกลับไปยังระบบ
                logCardChange(cardID, "ADD", level, adminID);
                logEvent("✅ Added access level '" + level + "' to card: " + cardID);
                updated = true;
            } else {
                System.out.println("⚠️ Access level already exists.");
            }
        }
        // 🔴 การลบสิทธิ์
        else if ("REVOKE".equalsIgnoreCase(action)) {
            boolean removed = accessLevels.removeIf(l -> l.trim().equalsIgnoreCase(level.trim())); // ลบสิทธิ์

            System.out.println("📌 ก่อนลบ: " + accessLevels);

            if (removed) {
                card.setAccessLevels(accessLevels); // อัปเดตการ์ดหลังการลบ
                this.updateCardInSystem(cardID, card); // อัปเดตข้อมูลกลับไปยังระบบ
                logCardChange(cardID, "REVOKE", level, adminID);
                logEvent("✅ Removed access level '" + level + "' from card: " + cardID);
                updated = true;
            } else {
                System.out.println("⚠️ สิทธิ์นี้ไม่มีในการ์ดหรือถูกลบไปแล้ว");
                // หากลบไม่ได้ จะไม่บันทึกการเปลี่ยนแปลง และแสดงข้อความว่าลบไม่สำเร็จ
            }
        }


        // 🟡 การแก้ไขสิทธิ์
        else if ("MODIFY".equalsIgnoreCase(action)) {
            // ตรวจสอบว่ามี level เดิมใน accessLevels หรือไม่
            if (accessLevels.contains(level)) {
                accessLevels.remove(level);  // ลบ level เดิม
                String newLevel = floor + " - " + room;  // สร้าง level ใหม่
                accessLevels.add(newLevel);  // เพิ่ม level ใหม่
                card.setAccessLevels(accessLevels);  // อัปเดตการ์ด
                this.updateCardInSystem(cardID, card); // อัปเดตข้อมูลกลับไปยังระบบ

                logCardChange(cardID, "MODIFY", level + " -> " + newLevel, adminID);
                logEvent("🔄 Access level changed for card: " + cardID + " from " + level + " to " + newLevel);
                updated = true;
            } else {
                System.out.println("❌ Access level not found to modify.");
                return false;
            }
        }

        // 🔑 อัปเดต PIN
        if (newPin != null && !newPin.equals(card.getPin())) {
            card.setPin(newPin);
            logCardChange(cardID, "CHANGE_PIN", "New PIN set", adminID);
            logEvent("🔑 PIN changed for card: " + cardID);
            updated = true;
        }

        // 📅 อัปเดตวันหมดอายุ
        if (newExpiryDate != null && !newExpiryDate.equals(card.getExpiryDate())) {
            card.setExpiryDate(newExpiryDate);
            logCardChange(cardID, "RENEW", "Expiry updated to " + newExpiryDate, adminID);
            logEvent("📅 Card renewed: " + cardID + ", new expiry: " + newExpiryDate);
            updated = true;
        }

        // ✅ ตรวจสอบว่ามีการเปลี่ยนแปลงจริงหรือไม่
        if (updated) {
            System.out.println("✅ Card updated successfully.");
            return true;
        } else {
            System.out.println("⚠️ No changes were made.");
            return false;
        }
    }

    public void changePin(String cardID, String newPin, String adminID) {
        AccessCard card = registeredCards.get(cardID);
        if (card != null) {
            card.setPin(newPin);  // อย่าลืมอัปเดต PIN ในข้อมูลการ์ด
            logCardChange(cardID, "CHANGE_PIN", "New PIN set", adminID);
            logEvent("PIN changed for card: " + cardID);
        }
    }

    public void renewCard(String cardID, LocalDateTime newExpiryDate, String adminID) {
        AccessCard card = registeredCards.get(cardID);
        if (card != null) {
            card.setExpiryDate(newExpiryDate);  // อย่าลืมอัปเดตวันหมดอายุ
            logCardChange(cardID, "RENEW", "Expiry extended to " + newExpiryDate, adminID);
            logEvent("Card renewed: " + cardID + ", new expiry: " + newExpiryDate);
        }
    }



    public void checkCard(String cardID) {
        AccessCard card = registeredCards.get(cardID);
        if (card != null) {
            System.out.println("[INFO] " + card.toString());
        } else {
            System.out.println("[ERROR] ไม่พบบัตร ID: " + cardID);
        }
    }

    public boolean removeCard(String cardID) {
        AccessCard card = registeredCards.remove(cardID);
        if (card != null) {
            logEvent("Card Removed: " + cardID);
            return true;
        } else {
            logEvent("Failed to remove card: " + cardID);
            return false;
        }
    }



    public void logEvent(String event) {
        auditLogs.add(LocalDateTime.now() + " - " + event);  // เพิ่มเหตุการณ์ลงใน auditLogs
        System.out.println("Event Logged: " + event);  // แสดงผลใน console
    }


    public void displayAuditLogs() {
        for (String log : auditLogs) {
            System.out.println(log);
        }
    }

    // ฟังก์ชันแสดงข้อมูลการเปลี่ยนแปลงสำหรับการ์ด
    public void displayCardChanges(String cardID) {
        List<String> changes = getCardChangesLog(cardID);
        if (changes.isEmpty()) {
            System.out.println("ไม่มีการเปลี่ยนแปลงสำหรับการ์ดนี้");
        } else {
            System.out.println("ข้อมูลการเปลี่ยนแปลงของการ์ด " + cardID + ":");
            for (String change : changes) {
                System.out.println(change);
            }
        }
    }

    // ฟังก์ชันสำหรับบันทึกการเปลี่ยนแปลงของการ์ด
    public void logCardChange(String cardID, String action, String levelOrRoom, String adminID) {
        // สมมติให้ใช้การพิมพ์ข้อมูลลงใน log file หรือในคอนโซล
        String logMessage = "Card ID: " + cardID + ", Action: " + action + ", Details: " + levelOrRoom + ", Admin ID: " + adminID;

        // เพิ่มการบันทึกข้อมูลลงใน changesLogMap
        changesLogMap.computeIfAbsent(cardID, k -> new ArrayList<>()).add(logMessage);

        // ตัวอย่างการพิมพ์ลงคอนโซล
        System.out.println("Log: " + logMessage);
    }

    // ตัวอย่างการบันทึก log เมื่อมีการเข้าถึงระบบ
    public void logAuditTrail(String action, String cardID, String details, String adminID) {
        String logEntry = "Action: " + action + ", Card ID: " + cardID + ", Details: " + details + ", Admin ID: " + adminID + ", Time: " + LocalDateTime.now();
        logEvent(logEntry);  // ใช้ logEvent ที่มีอยู่ในคลาสนี้แทน
    }



}


