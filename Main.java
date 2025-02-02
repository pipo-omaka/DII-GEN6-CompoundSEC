import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        AccessControlSystem system = new AccessControlSystem();

//        // Create a new access card
//        TimeBasedAccessCard card1 = new TimeBasedAccessCard("CARD123", LocalDateTime.now().plusDays(30));
//        card1.addAccessLevel("Low Floor");
//
//        // Register the card in the system
//        system.registerCard(card1);
//
//        // Modify permissions
//        system.modifyCard("CARD123", "ADD", "High Floor");
//
//        // Validate access
//        System.out.println("Access Granted? " + system.validateAccess("CARD123", "High Floor"));
//
//        // Show audit logs
//        system.showAuditLogs();

        Card a = new Card("a","123", "Manager");

        String result = a.getPrivateCode("a", "123");
        System.out.println(result);


        a.setStatus("Employee");
        String sum = a.getStatus();
        System.out.println(sum);

    }
}

