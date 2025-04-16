package cn.nap.download.client.view;

import cn.nap.download.client.manager.ClientManager;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MajorView extends AbstractView {
    public MajorView(Stage primaryStage, BorderPane root) {
        super(primaryStage, root);
    }

    public void initUI() {
        // 获取配置
        ClientManager clientManager = ClientManager.getInstance();
        if (clientManager.getApplicationProperties() == null) {
            InitializeView initializeView = new InitializeView(primaryStage, root);
            initializeView.setting();
            return;
        }
        DownloaderView downloaderView = new DownloaderView(primaryStage, root);
        downloaderView.initUI();
    }
}
