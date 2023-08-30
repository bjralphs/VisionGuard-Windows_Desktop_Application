package com.example.eye_reminder_system;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.awt.MenuItem;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class EyeReminderApp extends Application {

    private static final String ICON_PATH = "icon-16.png";

    private TrayIcon trayIcon;

    private static boolean isPaused = false;

    private static Reminder remind;


    // Setter method to alter the value of isPaused
    public static void setIsPaused(boolean value) {
        isPaused = value;
    }

    // Getter method to retrieve the value of isPaused (optional)
    public static boolean isPaused() {
        return isPaused;
    }

    private static void updateCheckMark(MenuItem parent, boolean checked) {
        String label = parent.getLabel();
        if (checked && !label.startsWith("✓ ")) {
            parent.setLabel("✓ " + label);

        } else if (!checked && label.startsWith("✓ ")) {
            parent.setLabel(label.substring(2));
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            Platform.exit();
        }
        createTrayIcon(primaryStage); //Add to Tray Icon
        Platform.setImplicitExit(false);
        remind = new Reminder();//Begin Reminder Timer
        remind.startReminder();
    }



    private void createTrayIcon(final Stage stage) {
        Platform.runLater(() -> {
            stage.setIconified(true);

            final SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ICON_PATH));
            trayIcon = new TrayIcon(image);

            StatusMenuController.statusProperty().addListener((observable, oldValue, newValue) -> {
                String formattedTime = StatusMenuController.formatTime(newValue.longValue());
                trayIcon.setToolTip("VisionGuard - " + formattedTime);
            });

            // Create the AWT popup menu and its menu items
            PopupMenu popup = new PopupMenu();
            MenuItem aboutItem = new MenuItem("About VisionGuard");
            MenuItem separator = new MenuItem("-");
            MenuItem enableItem = new MenuItem("Enable");
            PopupMenu disableMenu = new PopupMenu("Disable"); //Disable Menu

            enableItem.addActionListener(e -> {
                updateCheckMark(enableItem, true);
                updateCheckMark(disableMenu, false);
                remind.resetTimer();

            });
            MenuItem oneHourItem = new MenuItem("Disable for 1 hour");
            oneHourItem.addActionListener(e -> {
                updateCheckMark(enableItem, false);
                updateCheckMark(disableMenu, true);
                remind.addAnHour();
                // Your code to handle the 1-hour disable action
            });
            disableMenu.add(oneHourItem);
            MenuItem twoHourItem = new MenuItem("Disable for 2 hours");
            twoHourItem.addActionListener(e -> {
                // Your code to handle the 1-hour disable action
                updateCheckMark(enableItem, false);
                updateCheckMark(disableMenu, true);
                remind.addTwoHours();
            });
            disableMenu.add(twoHourItem);
            MenuItem threeHourItem = new MenuItem("Disable for 3 hours");
            threeHourItem.addActionListener(e -> {
                // Your code to handle the 1-hour disable action
                updateCheckMark(enableItem, false);
                updateCheckMark(disableMenu, true);
                remind.addThreeHours();
            });
            disableMenu.add(threeHourItem);
            MenuItem alldayItem = new MenuItem("Disable for rest of day");
            alldayItem.addActionListener(e -> {
                // Your code to handle the 1-hour disable action
                updateCheckMark(enableItem, false);
                updateCheckMark(disableMenu, true);
                remind.setTimeToEndOfDay();
            });
            disableMenu.add(alldayItem);

            MenuItem separator1 = new MenuItem("-"); // Separator bar
            MenuItem exitItem = new MenuItem("Exit Program");  // Exit program menu item

            // Add action listeners to the AWT menu items
            aboutItem.addActionListener(e -> {
                Platform.runLater(() -> {
                    About window = new About();
                    window.show();
                });
            });

            // Add action listeners to the AWT EXIT
            exitItem.addActionListener(e -> {
                Platform.runLater(() -> {
                    System.exit(0);
                });
            });

            //INITIALIZE TRAY ICON
            popup.add(aboutItem);
            popup.add(separator);  // Add separator
            popup.add(enableItem);
            popup.add(disableMenu);
            popup.add(separator1);  // Add separator
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Unable to setup tray icon: " + e.getMessage());
                e.printStackTrace();
            }

            // Create a MouseListener for the TrayIcon
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
                        Platform.runLater(() -> {
                            StatusMenu window = new StatusMenu();
                            window.show();
                        });
                    }
                }
            });
        });
    }
}