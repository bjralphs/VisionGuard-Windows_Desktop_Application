module com.example.eye_reminder_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.eye_reminder_system to javafx.fxml;
    exports com.example.eye_reminder_system;
}