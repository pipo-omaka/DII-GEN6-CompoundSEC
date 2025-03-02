//
//public  class Card {
//
//    private String username ;
//    private String password ;
//    private String status;
//    private String cardID;
//    private String accessLevel;
//
//    public Card() {
//        this.username = "xxxx";
//        this.password = "0000";
//    }
//
//    public  Card(String username, String password, String status){
//        this.username = username;
//        this.password = password;
//        this.status = status;
//    }
//
//    public String getPrivateCode(String name, String password){
//        if (name.equals(this.username) && password.equals(this.password)) {
//           return "Correct password";
//        }
//        else return "Wrong password";
//    }
//
//    public String getStatus(){
//        return status ;
//    }
//
//    public void setStatus(String status){
//        this.status=status;
//    }
//
//    public String getAccessLevel(){
//        return accessLevel ;
//    }
//
//    public void setAccessLevel(String accessLevel){
//        this.accessLevel = accessLevel;
//    }
//
//}

// Class to represent Users (employees, visitors, or special guests)
public class Card {
    private String userID;
    private String name;
    private AccessCard accessCard;

    public Card(String userID, String name, AccessCard accessCard) {
        this.userID = userID;
        this.name = name;
        this.accessCard = accessCard;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public AccessCard getAccessCard() {
        return accessCard;
    }

    public void assignAccessCard(AccessCard accessCard) {
        this.accessCard = accessCard;
    }
}

