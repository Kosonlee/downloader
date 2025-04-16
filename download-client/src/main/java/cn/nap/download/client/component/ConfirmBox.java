package cn.nap.download.client.component;

import cn.nap.download.client.util.ComponentUtil;
import cn.nap.download.client.util.ViewUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConfirmBox {
    public static final int YES = 1;
    public static final int NO = -1;
    public static final int BOTH = 0;


    public static void yes(Stage primaryStage, String text) {
        yesAndNo(primaryStage, text, YES);
    }

    public static void no(Stage primaryStage, String text) {
        yesAndNo(primaryStage, text, NO);
    }

    public static boolean both(Stage primaryStage, String text) {
        return yesAndNo(primaryStage, text, BOTH);
    }

    public static boolean yesAndNo(Stage primaryStage, String text, int type) {
        Stage stage = new Stage();
        AtomicBoolean result = new AtomicBoolean(false);

        VBox root = new VBox();

        HBox textHBox = new HBox();
        textHBox.setAlignment(Pos.CENTER);
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;-fx-wrap-text: true;-fx-text-fill: white");
        textHBox.getChildren().add(label);

        HBox buttonHBox = new HBox();
        buttonHBox.setAlignment(Pos.CENTER_RIGHT);
        buttonHBox.setSpacing(10);
        if (type >= 0) {
            Button yesButton = ComponentUtil.createBlueButton("确认");
            buttonHBox.getChildren().add(yesButton);
            yesButton.setOnAction(event -> {
                result.set(true);
                stage.close();
            });
        }
        if (type <= 0) {
            Button noButton = ComponentUtil.createGeneralButton("取消");
            buttonHBox.getChildren().add(noButton);
            noButton.setOnAction(event -> {
                result.set(false);
                stage.close();
            });
        }
        root.getChildren().addAll(textHBox, buttonHBox);
        VBox.setVgrow(textHBox, Priority.ALWAYS);
        root.setOnKeyReleased(event -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                result.set(false);
                stage.close();
            } else if (KeyCode.ENTER == event.getCode()) {
                result.set(true);
                stage.close();
            }
        });

        if (text.length() <= 16) {
            stage.setWidth(249);
            stage.setHeight(100);
        } else if (text.length() <= 18) {
            stage.setWidth(300);
            stage.setHeight(100);
        } else {
            stage.setWidth(300);
            stage.setHeight((int) Math.floor(text.length() / 18D) * 18 + 100);
        }
        ViewUtil.beforeShow(primaryStage, stage, root);
        stage.showAndWait();
        return result.get();
    }
}

