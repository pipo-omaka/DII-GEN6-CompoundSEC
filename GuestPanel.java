import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class GuestPanel {
    private JFrame frame;
    private AccessCard card;

    public GuestPanel(AccessCard card) {

        this.card = card;
        frame = new JFrame("Guest Panel");
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        updateInfo(infoArea);

        frame.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void updateInfo(JTextArea infoArea) {
        StringBuilder sb = new StringBuilder();
        sb.append("Card ID: ").append(card.getCardID()).append("\n");
        sb.append("Expiry Date: ").append(card.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Access Levels: ").append(String.join(", ", card.getAccessLevels())).append("\n");

        infoArea.setText(sb.toString());
    }
}
