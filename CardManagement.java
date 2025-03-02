
// Class to manage access cards
public class CardManagement {
    private static CardManagement instance;
    private AccessControlSystem accessControlSystem;

    private CardManagement(AccessControlSystem accessControlSystem) {
        this.accessControlSystem = accessControlSystem;
    }


    public static CardManagement getInstance() {
        if (instance == null) {
            synchronized (CardManagement.class) {
                if (instance == null) {
                    instance = new CardManagement(new AccessControlSystem());
                }
            }
        }
        return instance;
    }

//    public void addCard(AccessCard card) {
//        accessControlSystem.registerCard(card);
//    }
//
//    public void removeCard(String cardID) {
//        accessControlSystem.modifyCard(cardID, "REVOKE", "");
//    }
//
//    public void modifyCardAccess(String cardID, String action, String level) {
//        accessControlSystem.modifyCard(cardID, action, level);
//    }

//    public boolean validateCardAccess(String cardID, String level, String inputPin) {
//        return accessControlSystem.validateAccess(cardID, level, inputPin);
//    }

//    public void addZone(Zone zone) {
//        accessControlSystem.addZone(zone);
//    }
}

