import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Class to handle Audit Logs
public class AuditLog {
    private List<String> logs;

    public AuditLog() {
        this.logs = new ArrayList<>();
    }

    public void logEvent(String event) {
        logs.add(LocalDateTime.now() + " - " + event);
    }

    public void showLogs() {
        logs.forEach(System.out::println);
    }
}

