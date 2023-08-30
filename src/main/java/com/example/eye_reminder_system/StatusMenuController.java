package com.example.eye_reminder_system;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Hyperlink;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;

public class StatusMenuController {

    boolean isPaused = EyeReminderApp.isPaused();

    @FXML
    private Hyperlink githubLink;

    @FXML
    private void openLink() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(githubLink.getEllipsisString()));
            }
        } catch (Exception e) {
            System.err.println("Error opening the GitHub link in browser: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private Text toggleText;

    @FXML
    private Text timeText;

    @FXML
    private CheckBox silentNotificationsCheckBox;

    private static boolean isChecked = false;
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void terminateProgram() {
        System.exit(0);  // Terminate the JavaFX application
    }

    public static SimpleLongProperty remainingTime = new SimpleLongProperty();

    public static void setRemainingTime(){ remainingTime.set(Reminder.getRemainder()); }

    public static SimpleLongProperty statusProperty() {
        return remainingTime;
    }


    // Initialize your controller (similar to a constructor for JavaFX)
    @FXML
    public void initialize() {
        try {
            isChecked = silentNotificationsCheckBox.isSelected(); // Get initial value
        }
        catch(NullPointerException e){
            isChecked = false;
        }

        silentNotificationsCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isChecked = newValue;
                // Optional: You can also print the value for debugging
                // System.out.println("Checkbox is " + (isChecked ? "checked" : "unchecked"));
                saveStateToFile();
            }
        });

        timeText.textProperty().bind(Bindings.createStringBinding(() ->
                        formatTime(remainingTime.get()),
                remainingTime
        ));



        // Initial update
        updateStatusText();
    }

    public static boolean isChecked() {

        return isChecked;
    }
    private void updateStatusText() {
        String status = isPaused ? "Paused" : "Active";
        toggleText.setText(status);
        loadStateFromFile();

        //String newTime = formatTime(Reminder.getRemainder());
        //timeText.setText(newTime);

        remainingTime.set(Reminder.getRemainder());
        updateTextColorBasedOnStatus();
    }
    private void updateTextColorBasedOnStatus() { //Set to run after updateStatusText
        if ("Active".equalsIgnoreCase(toggleText.getText())) {
            toggleText.setFill(Color.GREEN);
        } else if ("Paused".equalsIgnoreCase(toggleText.getText())) {
            toggleText.setFill(Color.RED);
        } else {
            toggleText.setFill(Color.BLACK);  // Default color or any other color you want.
        }
    }

    public static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long remainingSeconds = totalSeconds % 3600;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;

        StringBuilder formattedTime = new StringBuilder();
        if (hours > 0) {
            formattedTime.append(hours).append(" hours ");
        }
        if (hours > 0 || minutes > 0) {
            formattedTime.append(minutes).append(" minutes ");
        }
        formattedTime.append(seconds).append(" seconds");

        return formattedTime.toString().trim();
    }


    private void saveStateToFile() {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File("config.txt");

        // Read all lines from the file
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Find the first line that doesn't start with '#' and modify it
        boolean lineModified = false;
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).trim().startsWith("#")) {
                lines.set(i, String.valueOf(silentNotificationsCheckBox.isSelected()));
                lineModified = true;
                break;
            }
        }
        if (!lineModified) { // If no line was modified, add a new line
            lines.add(String.valueOf(silentNotificationsCheckBox.isSelected()));
        }

        // Write the lines back to the file
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadStateFromFile() {
        File file = new File("config.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("#")) {
                        boolean selected = Boolean.parseBoolean(line.trim());
                        isChecked = selected;
                        try{
                            silentNotificationsCheckBox.setSelected(selected);
                        }
                        catch(Exception e)
                        {
                            //window tray hasnt been opened yet/initialized
                            continue;
                        }
                        break; // We found our line, so we can break out of the loop
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void loadState() {
        File file = new File("config.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("#")) {
                        isChecked = Boolean.parseBoolean(line.trim());
                        break; // We found our line, so we can break out of the loop
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
