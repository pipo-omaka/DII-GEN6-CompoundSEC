import java.time.LocalDateTime;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {


//        \======================Access and Add Access++++++++++++++++++++++++++/

//        AccessControlSystem system = new AccessControlSystem();
//
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
//        // Create Zones in the zoo
//        Zone zone1 = new Zone("Z001", "Animal Kingdom");
//        Zone zone2 = new Zone("Z002", "Tropical Garden");
//
//        // Add Zones to the system
//        system.addZone(zone1);
//        system.addZone(zone2);
//
//        // Validate access for different zones and floors
//        System.out.println("Access Granted to Low Floor? " + system.validateAccess("CARD123", "Low Floor"));
//        System.out.println("Access Granted to High Floor? " + system.validateAccess("CARD123", "High Floor"));
//
//        // Show audit logs
//        system.showAuditLogs();

//        \======================Edit and Add rights++++++++++++++++++++++++++/

//        AccessControlSystem system = new AccessControlSystem();
//        system.setAccessVerificationStrategy(new MultiFactorAuthentication()); // ใช้ Multi-Factor
//
//        // สร้างบัตรผ่าน
//        TimeBasedAccessCard card1 = new TimeBasedAccessCard("CARD123", LocalDateTime.now().plusDays(30));
//        card1.addAccessLevel("Low Floor");
//        system.registerCard(card1);
//
//        // ให้สิทธิ์ชั่วคราว
//        AccessCard tempCard = new AccessCard.TemporaryPermissionDecorator(card1, "Special zone", LocalDateTime.now().plusDays(1));
//
//        // ทดสอบการเข้าถึง
//        System.out.println("Access to Low Floor: " + system.validateAccess("CARD123", "Low Floor"));
//        System.out.println("Access to Special zone (Temporary): " + tempCard.hasAccess("Special zone"));
//
//        //Singeleton
//        //มีข้อผิดพลาด🖍️🖍️🆘
//        CardManager cardManager = CardManager.getInstance(); // รับ instance ของ CardManager
//        AccessCard newCard = new AccessCard("CARD123", "John Doe"); // สร้างบัตรใหม่
//        cardManager.addCard(newCard);
//
//        // เพิ่มสิทธิ์การเข้าถึง
//        cardManager.modifyCardAccess("CARD123", "GRANT", "Zoo Area 1");
//
//        // ตรวจสอบการเข้าถึง
//        boolean canAccess = cardManager.validateCardAccess("CARD123", "Zoo Area 1");
//        System.out.println("Can access Zoo Area 1: " + canAccess); // ผลลัพธ์จะเป็น true หรือ false

//        //+++++++++++++++++==============Test🔍===========++++++++++++++++++
//        AccessControlSystem system = new AccessControlSystem();
////        system.setAccessVerificationStrategy(new MultiFactorAuthentication()); // ใช้ Multi-Factor
//        system.setAccessVerificationStrategy(new BasicAuthentication());
//
//        // สร้างบัตร Time-Based
//        TimeBasedAccessCard card1 = new TimeBasedAccessCard("CARD123", "1234", LocalDateTime.now().plusDays(30));
//        card1.addAccessLevel("Low Floor");
//        system.registerCard(card1);
//
//        // ให้สิทธิ์ชั่วคราว
//        AccessCard tempCard = new TemporaryPermissionDecorator(card1, "Special zone", LocalDateTime.now().plusDays(1));
//
//        // ทดสอบการเข้าถึง
//        System.out.println("Access to Low Floor: " + system.validateAccess("CARD123", "Low Floor", "1234"));
//        System.out.println("Access to Special zone (Temporary): " + tempCard.hasAccess("Special zone", "1234"));
//
//        // ====== ใช้ CardManager ======
//        CardManager cardManager = CardManager.getInstance();
//        AccessCard newCard = new TimeBasedAccessCard("CARD124", "5678", LocalDateTime.now().plusDays(30));
//        cardManager.addCard(newCard);
//
//        // ก่อนเพิ่มสิทธิ์
//        System.out.println("Before Granting Access:");
//        System.out.println("Can access Zoo Area 1 (before grant): " + cardManager.validateCardAccess("CARD124", "Zoo Area 1", "5678"));
//
//        // เพิ่มสิทธิ์
//        cardManager.modifyCardAccess("CARD124", "GRANT", "Zoo Area 1");
//
//        // หลังเพิ่มสิทธิ์
//        System.out.println("After Granting Access:");
//        System.out.println("Can access Zoo Area 1 (after grant): " + cardManager.validateCardAccess("CARD124", "Zoo Area 1", "5678"));
//
//        // แสดง log
//        system.showAuditLogs();


        //+++++++++++++++++++++++++++=========🚀GUI========++++++++++++++++++++++
        AccessControlSystem system = new AccessControlSystem();

        TimeBasedAccessCard managerCard = new TimeBasedAccessCard("ADMIN001", "1234", LocalDateTime.now().plusDays(30));
        managerCard.addAccessLevel("Manager");
        managerCard.addAccessLevel("Floor 1 - Room 101");  // ✅ เพิ่มสิทธิ์เข้า Floor 1 Room 101
        system.registerCard(managerCard);

        TimeBasedAccessCard guestCard1 = new TimeBasedAccessCard("GUEST001", "5678", LocalDateTime.now().plusDays(10));
        guestCard1.addAccessLevel("Low Floor");
        guestCard1.addAccessLevel("Floor 1 - Room 102"); // ✅ เพิ่มสิทธิ์เข้า Floor 1 Room 102
        system.registerCard(guestCard1);

        TimeBasedAccessCard guestCard2 = new TimeBasedAccessCard("GUEST002", "5678", LocalDateTime.now().plusDays(10));
        guestCard2.addAccessLevel("Medium Floor");
        guestCard2.addAccessLevel("Floor 2 - Room 201"); // ✅ เพิ่มสิทธิ์เข้า Floor 2 Room 201
        system.registerCard(guestCard2);

        system.displayCardChanges("ADMIN001");
        SwingUtilities.invokeLater(() -> new AccessControlGUI(system));
        System.out.println("Manager Access Levels: " + managerCard.getAccessLevels());








//        // Create a new access card
//        TimeBasedAccessCard card1 = new TimeBasedAccessCard("CARD123","1234", LocalDateTime.now().plusDays(30));
//        card1.addAccessLevel("Low Floor");
//
//        // Register the card in the system
//        system.registerCard(card1);
//
//        // Modify permissions
//        system.modifyCard("CARD123", "REVOKE", "Low Floor");
//
//        // Validate access
//        System.out.println("Access Granted? " + system.validateAccess("CARD123", "Medium Floor"));
//
//        // Show audit logs
//        system.showAuditLogs();


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




