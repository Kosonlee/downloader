package cn.nap.download.client.util;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicBoolean;

public class ViewUtil {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("线程休眠异常: " + e.getMessage());
        }
    }

    public static void centerOnParent(Stage primaryStage, Stage stage) {
        stage.setX(primaryStage.getX() + (primaryStage.getWidth() - stage.getWidth()) / 2);
        stage.setY(primaryStage.getY() + (primaryStage.getHeight() - stage.getHeight()) / 2);
    }

    public static void bottomRightOnParent(Stage primaryStage, Stage stage) {
        stage.setX(primaryStage.getX() + primaryStage.getWidth() - stage.getWidth() - 20);
        stage.setY(primaryStage.getY() + primaryStage.getHeight() - stage.getHeight() - 20);
    }

    public static void beforeShow(Stage primaryStage, Stage stage, Pane root) {
        root.setStyle("-fx-padding: 10;-fx-spacing: 10; -fx-background-color: rgb(31, 31, 31);-fx-border-color: rgb(172, 172, 172);-fx-border-width: 1;-fx-background-radius: 10;-fx-border-radius: 10");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        centerOnParent(primaryStage, stage);
    }

    /**
     * 因为和1.8不一样，已经调用不了runAndWait了，所以自己封装一个类似的功能
     */
    public static void runAndWait(Runnable runnable) {
        AtomicBoolean running = new AtomicBoolean(true);
        Platform.runLater(() -> {
            runnable.run();
            running.set(false);
        });
        while (running.get()) {
            sleep(10);
        }
    }
}
