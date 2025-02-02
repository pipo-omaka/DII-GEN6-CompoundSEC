import java.time.LocalDateTime;
import java.util.*;

// Base class for Access Cards
abstract class AccessCard {
    private String cardID;
    private Set<String> accessLevels;
    private LocalDateTime expiryDate;

    public AccessCard(String cardID, LocalDateTime expiryDate) {
        this.cardID = cardID;
        this.expiryDate = expiryDate;
        this.accessLevels = new HashSet<>();
    }

    public void addAccessLevel(String level) {
        accessLevels.add(level);
    }

    public void revokeAccessLevel(String level) {
        accessLevels.remove(level);
    }

    public boolean hasAccess(String level) {
        return accessLevels.contains(level) && LocalDateTime.now().isBefore(expiryDate);
    }

    public abstract void encryptCardData();

    public String getCardID() {
        return cardID;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}

// Subclass for Time-Based Encrypted Access Cards
class TimeBasedAccessCard extends AccessCard {
    private String encryptedData;

    public TimeBasedAccessCard(String cardID, LocalDateTime expiryDate) {
        super(cardID, expiryDate);
        encryptCardData();
    }

    @Override
    public void encryptCardData() {
        this.encryptedData = "Encrypted:" + getCardID() + "@" + getExpiryDate().toString();
    }

    public String getEncryptedData() {
        return encryptedData;
    }
}

// Access Control System
class AccessControlSystem {
    private Map<String, AccessCard> registeredCards;
    private List<String> auditLogs;

    public AccessControlSystem() {
        registeredCards = new HashMap<>();
        auditLogs = new ArrayList<>();
    }

    public void registerCard(AccessCard card) {
        registeredCards.put(card.getCardID(), card);
        logEvent("Card Registered: " + card.getCardID());
    }

    public void modifyCard(String cardID, String action, String level) {
        AccessCard card = registeredCards.get(cardID);
        if (card != null) {
            if ("ADD".equalsIgnoreCase(action)) {
                card.addAccessLevel(level);
                logEvent("Access Granted: Card " + cardID + " -> Level " + level);
            } else if ("REVOKE".equalsIgnoreCase(action)) {
                card.revokeAccessLevel(level);
                logEvent("Access Revoked: Card " + cardID + " -> Level " + level);
            }
        }
    }

    public boolean validateAccess(String cardID, String level) {
        AccessCard card = registeredCards.get(cardID);
        boolean access = card != null && card.hasAccess(level);
        logEvent("Access Attempt: Card " + cardID + " -> Level " + level + " -> " + (access ? "GRANTED" : "DENIED"));
        return access;
    }

    private void logEvent(String event) {
        auditLogs.add(LocalDateTime.now() + " - " + event);
    }

    public void showAuditLogs() {
        auditLogs.forEach(System.out::println);
    }
}





