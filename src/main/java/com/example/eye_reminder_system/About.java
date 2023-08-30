package com.example.eye_reminder_system;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import java.io.IOException;
import java.util.Objects;

public class About {

    private static final String FXML_PATH = "About.fxml";
    private static final String WINDOW_TITLE = "VisionGuard - Protect Your Sight";
    private static final String ICON_PATH = "/icon-16.png";
    private static final double WINDOW_WIDTH = 620;
    private static final double WINDOW_HEIGHT = 400;
    private static final double WINDOW_OFFSET_X = 700;
    private static final double WINDOW_OFFSET_Y = 500;

    private Stage statusStage;
    private static About instance; // Singleton instance

    public About() {
        init();
    }

    public static About getInstance() {
        if (instance == null) {
            instance = new About();
        }
        return instance;
    }



    private void init() {
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

            statusStage = new Stage();
            statusStage.setTitle(WINDOW_TITLE);
            statusStage.setScene(scene);
            statusStage.setResizable(false);

            // Set custom icon
            Image customIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));
            statusStage.getIcons().add(customIcon);
            statusStage.setAlwaysOnTop(true);  // Make sure the window is always on top
            statusStage.toFront();             // Bring the window to the front

            // Position window to bottom right
            positionWindowBottomRight();

        } catch (IOException e) {
            System.err.println("Error initializing the About window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void positionWindowBottomRight() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        statusStage.setX(screenBounds.getMaxX() - WINDOW_OFFSET_X);
        statusStage.setY(screenBounds.getMaxY() - WINDOW_OFFSET_Y);
    }

    public void show() {
        if (statusStage != null) {
            if (statusStage.isShowing()) {
                // Bring the existing window to front if it's already open
                statusStage.toFront();
            } else {
                // Otherwise, show the window
                statusStage.show();
            }
        }
    }
}