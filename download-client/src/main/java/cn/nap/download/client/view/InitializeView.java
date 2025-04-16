package cn.nap.download.client.view;

import cn.nap.download.client.component.ConfirmBox;
import cn.nap.download.client.component.ProgressLoading;
import cn.nap.download.client.manager.ClientManager;
import cn.nap.download.client.util.ComponentUtil;
import cn.nap.download.client.util.ViewUtil;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class InitializeView extends AbstractView {
    private final AtomicInteger stepIndex = new AtomicInteger(1);
    private final StepView stepView;

    public InitializeView(Stage primaryStage, BorderPane root) {
        super(primaryStage, root);
        this.stepView = new StepView(primaryStage, root);
    }

    public void setting() {
        HBox top = new HBox();
        top.setStyle("-fx-alignment: center;-fx-padding: 5");
        Label titleLabel = ComponentUtil.createTitleLabel("");
        top.getChildren().add(titleLabel);

        HBox bottom = new HBox();
        bottom.setStyle("-fx-alignment: center;-fx-padding: 10;-fx-spacing: 10");
        Button lastStep = ComponentUtil.createBlueButton("上一步");
        Button nextStep = ComponentUtil.createBlueButton("下一步");
        Region spacer = new Region();
        bottom.getChildren().addAll(lastStep, spacer, nextStep);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        root.setTop(top);
        root.setBottom(bottom);

        switchStep(top, bottom);
        lastStep.setOnAction(event -> toLastStep(top, bottom));
        nextStep.setOnAction(event -> toNextStep(top, bottom));
    }

    private void switchStep(HBox top, HBox bottom) {
        Label title = (Label) top.getChildren().getFirst();
        title.setText(null);

        Button lastStep = (Button) bottom.getChildren().getFirst();
        Button nextStep = (Button) bottom.getChildren().getLast();
        lastStep.setVisible(true);
        nextStep.setVisible(true);
        nextStep.setText("下一步");

        root.setCenter(null);
        switch (stepIndex.get()) {
            case 1 -> stepView.step1(bottom);
            case 2 -> stepView.step2(top);
            case 3 -> stepView.step3(top, bottom);
            case 4 -> stepView.step4(top);
            case 5 -> stepView.step5(top, bottom);
        }
    }

    private void toLastStep(HBox top, HBox bottom) {
        if (stepIndex.get() > 1) {
            stepIndex.decrementAndGet();
        }
        switchStep(top, bottom);
    }

    private void toNextStep(HBox top, HBox bottom) {
        if (stepIndex.get() < 5) {
            stepIndex.incrementAndGet();
        } else {
            saveProperties();
        }
        switchStep(top, bottom);
    }

    private void saveProperties() {
        if (ClientManager.getInstance().getPrepareProperties() == null) {
            ConfirmBox.yes(primaryStage, "你有步骤尚未完成");
            return;
        }
        ProgressLoading loading = new ProgressLoading(primaryStage);
        loading.add("正在保存配置...", () -> {
            try {
                ClientManager.getInstance().saveProperties();
            } catch (Exception e) {
                ViewUtil.runAndWait(() -> ConfirmBox.yes(primaryStage, "保存配置发生异常"));
                throw e;
            }
        });
        loading.add("正在更新UI...", () -> ViewUtil.runAndWait(() -> {
            DownloaderView downloaderView = new DownloaderView(primaryStage, root);
            downloaderView.initUI();
        }));
        loading.execute();
    }
}
