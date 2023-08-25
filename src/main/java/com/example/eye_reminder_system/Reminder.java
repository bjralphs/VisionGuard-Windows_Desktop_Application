package com.example.eye_reminder_system;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import javafx.util.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder {
    private Timer timer;

    private static long placeholder;
    private static long periodInMilliseconds = 20 * 60 * 1000; // 20 minutes

    private final javafx.scene.image.Image customIcon = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon-16.png")));

    public static long getRemainder(){
        return placeholder;
    }

    public void startReminder() {
        if (timer != null) {
            timer.cancel();  // Cancel the previous timer if exists
        }
        Platform.setImplicitExit(false);
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendReminderNotification();
            }
        },periodInMilliseconds, periodInMilliseconds);
        placeholder = periodInMilliseconds;
        // Task to print a string every second
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(placeholder <= 0){
                    placeholder = periodInMilliseconds;
                }
                placeholder -= 1000;
                //Troubleshooting
                //System.out.println(periodInMilliseconds);
                //System.out.println(placeholder);
                StatusMenuController.setRemainingTime();
            }
        }, 0, 1000); // Schedule every second
    }

    public void resetTimer() {
        periodInMilliseconds = 20 * 60 * 1000;
        startReminder();
        EyeReminderApp.setIsPaused(false);
    }
    public void addAnHour() {
        resetTimer();
        periodInMilliseconds += 60 * 60 * 1000;  // Add an hour
        startReminder();  // Restart the timer with the new period
        EyeReminderApp.setIsPaused(true);
    }

    public void addTwoHours() {
        resetTimer();
        periodInMilliseconds += 2 *  60 * 60 * 1000;  // Add an hour
        startReminder();  // Restart the timer with the new period
        EyeReminderApp.setIsPaused(true);
    }

    public void addThreeHours() {
        resetTimer();
        periodInMilliseconds += 3*  60 * 60 * 1000;  // Add an hour
        startReminder();  // Restart the timer with the new period
        EyeReminderApp.setIsPaused(true);
    }

    public void setTimeToEndOfDay() {
        LocalTime now = LocalTime.now();
        Instant nowInstant = now.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        Instant endOfDayInstant = LocalTime.MAX.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();

        // Calculate the difference in milliseconds using JavaFX Duration
        Duration durationUntilEndOfDay = Duration.millis(endOfDayInstant.toEpochMilli() - nowInstant.toEpochMilli());

        EyeReminderApp.setIsPaused(true);

        periodInMilliseconds = (long) durationUntilEndOfDay.toMillis();
        startReminder();  // Restart the timer with the new period
    }


    private void sendReminderNotification() {
        Platform.setImplicitExit(false);
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::sendReminderNotification);
            return;
        }
        Platform.setImplicitExit(false);
        EyeReminderApp.setIsPaused(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Look 20 feet away for 20 seconds!");
        alert.setTitle("VisionGuard - Reminder");
        alert.setHeaderText(null);

        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(customIcon);
        StatusMenuController.loadState();

        if (!StatusMenuController.isChecked()) {
            playNotificationSound();

        }

        alert.show();
        //playNotificationSound();

        // Set up the pause to close the alert after 30 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(30));
        delay.setOnFinished(event -> alert.close());
        delay.play();

    }

    private void playNotificationSound() {
        try {
            URL soundResource = getClass().getResource("/notification.wav");
            assert soundResource != null;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundResource);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

}
