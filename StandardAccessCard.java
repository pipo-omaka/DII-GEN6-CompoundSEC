import java.time.LocalDateTime;

public class StandardAccessCard extends AccessCard {
    private String floor;
    private String room;

    public StandardAccessCard(String cardID, String pin, String floor, String room, LocalDateTime expiryDate) {
        super(cardID, pin, expiryDate);
        this.floor = floor;
        this.room = room;
    }

    @Override
    public void encryptCardData() {
        // เพิ่มการเข้ารหัสข้อมูลการ์ดที่นี่
        System.out.println("การ์ด " + getCardID() + " ได้รับการเข้ารหัสข้อมูลเรียบร้อยแล้ว.");
    }

    // เพิ่มฟังก์ชันเพื่อให้สามารถเข้าถึงข้อมูล floor และ room
    public String getFloor() {
        return floor;
    }

    public String getRoom() {
        return room;
    }
}


