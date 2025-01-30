
public  class Card {

    private String username ="a";
    private String password ="123";
    private String status;
    private String cardID;
    private int accessLevel;

    public  Card(String username, String password){
        this.username=username;
        this.password=password;
    }

    public String getPrivateCode(String name, String password){
        if (name.equals(this.username) && password.equals(this.password)) {
           return "Correct password";
        }
        else return "Wrong password";


    }

}
