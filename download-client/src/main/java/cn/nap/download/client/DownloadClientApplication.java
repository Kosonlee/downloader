package cn.nap.download.client;

import cn.nap.download.client.util.ComponentUtil;
import cn.nap.download.client.view.MajorView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DownloadClientApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: rgb(43, 43, 43)");

        try {
            MajorView majorView = new MajorView(stage, root);
            majorView.initUI();
        } catch (Exception e) {
            log.error("初始化失败：{}", e.getMessage(), e);
            root.getChildren().clear();
            VBox center = new VBox();
            center.setAlignment(Pos.CENTER);
            center.setSpacing(10);
            center.setPadding(new Insets(5));
            Label titleLabel = ComponentUtil.createTitleLabel("程序运行发生未知异常：");
            Label linearGradientLabel = ComponentUtil.createLinearGradientLabel(e.getMessage());
            linearGradientLabel.setWrapText(true);
            center.getChildren().addAll(titleLabel, linearGradientLabel);

            root.setCenter(center);
        }

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setWidth(400);
        stage.setHeight(300);
        stage.setTitle("下载器&更新器 by Nap");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}