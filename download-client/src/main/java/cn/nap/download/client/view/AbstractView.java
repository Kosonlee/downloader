package cn.nap.download.client.view;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AbstractView {
    protected final Stage primaryStage;
    protected final BorderPane root;

    public AbstractView(Stage primaryStage, BorderPane root) {
        this.primaryStage = primaryStage;
        this.root = root;
    }

}
