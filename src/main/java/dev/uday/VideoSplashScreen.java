package dev.uday;

import dev.uday.GUI.MainFrame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoSplashScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        String videoPath = getClass().getResource("/splash.mp4").toExternalForm();
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        StackPane root = new StackPane(mediaView);
        Scene scene = new Scene(root, 854, 480);

        // Bind the MediaView size to the Scene size
        mediaView.fitWidthProperty().bind(scene.widthProperty());
        mediaView.fitHeightProperty().bind(scene.heightProperty());
        mediaView.setPreserveRatio(true);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Splash Screen");
        // make the window undecorated and round the corners
        primaryStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setWidth(852);
        primaryStage.setHeight(479);
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Prevent the default close action
            mediaPlayer.stop(); // Stop the video playback
            primaryStage.close(); // Close the splash screen
        });
        primaryStage.show();

        mediaPlayer.setOnEndOfMedia(() -> {
            primaryStage.close();
            // Launch the main application
            MainFrame.setMainFrame();
            // Bring the main application window to the front
            MainFrame.mainFrame.toFront();
        });

        mediaPlayer.play();
    }

    public static void showSplashScreen() {
        launch();
    }
}