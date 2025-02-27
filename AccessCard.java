import java.time.LocalDateTime;
import java.util.*;

//====== Base Class ======

// Base class for Access Cards
abstract class AccessCard {
    private String cardID;
    private String pin;
    private Set<String> accessLevels;
    private LocalDateTime expiryDate;

    public AccessCard(String cardID, String pin, LocalDateTime expiryDate) {
        this.cardID = cardID;
        this.pin = pin;
        this.expiryDate = expiryDate;
        this.accessLevels = new HashSet<>();
    }

    public void addAccessLevel(String level) {
        accessLevels.add(level);
    }

    public void revokeAccessLevel(String level) {
        accessLevels.remove(level);
    }

    public boolean validatePIN(String inputPin) {
        return this.pin.equals(inputPin);
    }


    public boolean hasAccess(String level, String inputPin) {
        return validatePIN(inputPin) && accessLevels.contains(level) && LocalDateTime.now().isBefore(expiryDate);
    }


    public abstract void encryptCardData();

    public String getCardID() {
        return cardID;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public Set<String> getAccessLevels() {
        return accessLevels;
    }

    public String getAccessLevelCategory() {
        int accessCount = accessLevels.size();
        if (accessCount == 0) return "Low";
        else if (accessCount <= 2) return "Medium";
        else return "High";
    }

    @Override
    public String toString() {
        return "CardID: " + cardID + ", Levels: " + accessLevels.toString() + ", Expiry: " + expiryDate;
    }



    public void removeAccessLevel(String level) {
    }


    public boolean hasAccessLevel(String level) {
        return false;
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
    public boolean hasAccess(String level, String inputPin) {
        if (LocalDateTime.now().isBefore(expiry) && level.equals(tempPermission)) {
            return true;
        }
        return decoratedCard.hasAccess(level, inputPin);
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
        this.encryptedData = "Encrypted:" + getCardID() + "@" + getExpiryDate().toString();
    }

    public String getEncryptedData() {
        return encryptedData;
    }
}

//====== Strategy Pattern for Authentication ======
interface AccessVerificationStrategy {
    boolean verify(AccessCard card, String level, String inputPin);
}

// Concrete Strategy 1: Basic Authentication (ตรวจเฉพาะหมายเลขบัตร)
class BasicAuthentication implements AccessVerificationStrategy {
    @Override
    public boolean verify(AccessCard card, String level, String inputPin) {
        return card.hasAccess(level, inputPin);
    }
}

//// Concrete Strategy 2: Multi-Factor Authentication
//class MultiFactorAuthentication implements AccessVerificationStrategy {
//    @Override
//    public boolean verify(AccessCard card, String level, String inputPin) {
//        return card.hasAccess(level, inputPin) && Math.random() > 0.5; // 50% โอกาสสำเร็จ
//    }
//}

//====== Access Control System ======
class AccessControlSystem {
    private Map<String, AccessCard> registeredCards;
    private List<String> auditLogs;
    private Map<String, String> zones;
    private AccessVerificationStrategy strategy;
    private Map<String, AccessCard> cardDatabase; // เก็บข้อมูลบัตร

    public AccessControlSystem() {
        registeredCards = new HashMap<>();
        auditLogs = new ArrayList<>();
        zones = new HashMap<>();
        this.strategy = new BasicAuthentication();
        cardDatabase = new HashMap<>();
    }

    public void deregisterCard(String cardID) {
        if (registeredCards.containsKey(cardID)) {
            registeredCards.remove(cardID);
            logEvent("Card Deregistered: " + cardID);
        }
    }


    public void registerCard(AccessCard card) {
        registeredCards.put(card.getCardID(), card);
        logEvent("Card Registered: " + card.getCardID());
    }

    public AccessCard getCard(String cardID) {
        return registeredCards.get(cardID);
    }

    public List<AccessCard> getAllCards() {
        return new ArrayList<>(registeredCards.values());
    }


    public boolean modifyCard(String cardID, String action, String level) {
        AccessCard card = cardDatabase.get(cardID);

        if (card == null) {
            System.out.println("[ERROR] ไม่พบบัตร ID: " + cardID);
            return false;
        }

        if (action.equals("ADD")) {
            card.addAccessLevel(level);
            System.out.println("[SUCCESS] เพิ่มสิทธิ์ให้ " + cardID + " ระดับ: " + level);
            return true;
        } else if (action.equals("REVOKE")) {
            if (card.hasAccessLevel(level)) { // ตรวจสอบก่อนว่ามีสิทธิ์นี้ไหม
                card.removeAccessLevel(level);
                System.out.println("[SUCCESS] ถอนสิทธิ์ " + level + " จากบัตร " + cardID);
                return true;
            } else {
                System.out.println("[ERROR] บัตรนี้ไม่มีสิทธิ์ระดับ: " + level);
                return false;
            }
        }

        System.out.println("[ERROR] คำสั่งไม่ถูกต้อง: " + action);
        return false;
    }



    public void setAccessVerificationStrategy(AccessVerificationStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean validateAccess(String cardID, String level, String inputPin) {
        AccessCard card = registeredCards.get(cardID);
        if (card == null) {
            System.out.println("Card not found: " + cardID);
            return false;
        }

        boolean access = strategy.verify(card, level, inputPin);
        logEvent("Access Attempt: Card " + cardID + " -> Level " + level + " -> " + (access ? "GRANTED" : "DENIED"));
        return access;
    }

    public void addZone(String zoneID, String zoneName) {
        zones.put(zoneID, zoneName);
        logEvent("Zone Added: " + zoneName);
    }

    public String getZone(String zoneID) {
        return zones.get(zoneID);
    }

    private void logEvent(String event) {
        auditLogs.add(LocalDateTime.now() + " - " + event);
    }

    public void showAuditLogs() {
        auditLogs.forEach(System.out::println);
    }

    public String getAuditLogs() {
        return String.join("\n", auditLogs);
    }

}






