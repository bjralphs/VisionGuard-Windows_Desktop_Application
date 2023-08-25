package com.example.eye_reminder_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import java.awt.*;
import java.net.URI;

public class AboutController {

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
    private void handleCloseButtonAction(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
