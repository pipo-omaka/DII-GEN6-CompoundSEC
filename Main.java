import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        AccessControlSystem system = new AccessControlSystem();

        // Create a new access card
        TimeBasedAccessCard card1 = new TimeBasedAccessCard("CARD123", LocalDateTime.now().plusDays(30));
        card1.addAccessLevel("Low Floor");

        // Register the card in the system
        system.registerCard(card1);

        // Modify permissions
        system.modifyCard("CARD123", "REVOKE", "Low Floor");

        // Validate access
        System.out.println("Access Granted? " + system.validateAccess("CARD123", "Medium Floor"));

        // Show audit logs
        system.showAuditLogs();


//        //add && edit
//        Card a = new Card("a","123", "Manager");
//
//        System.out.println(a.getPrivateCode("a", "123"));
//
//        System.out.println(a.getStatus());
//
//        a.setStatus("Employee");
//
//        System.out.println(a.getStatus());

    }
}

