package cn.nap.download.client.view;

import cn.nap.download.client.component.TextInput;
import cn.nap.download.client.constant.DownloadPropertiesType;
import cn.nap.download.client.manager.ClientManager;
import cn.nap.download.client.service.InitializeService;
import cn.nap.download.client.util.ComponentUtil;
import cn.nap.download.common.constant.DefaultConstant;
import cn.nap.download.common.download.DownloadManager;
import cn.nap.download.common.factory.DownloadFactory;
import cn.nap.download.common.properties.ApplicationProperties;
import cn.nap.download.common.util.CommonUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class StepView extends AbstractView{
    private ToggleGroup toggleGroup;
    private ApplicationProperties applicationProperties;

    public StepView(Stage primaryStage, BorderPane root) {
        super(primaryStage, root);
    }

    public void step1(HBox bottom) {
        HBox center = new HBox();
        center.setStyle("-fx-alignment: center;-fx-padding: 10");
        Label titleLabel = ComponentUtil.createTitleLabel("首次启动，你需要进行一些配置");
        center.getChildren().add(titleLabel);
        root.setCenter(center);

        Button lastStep = (Button) bottom.getChildren().getFirst();
        lastStep.setVisible(false);
    }

    public void step2(HBox top) {
        Label title = (Label) top.getChildren().getFirst();
        title.setText("选择一个下载器");

        ScrollPane center = ComponentUtil.createScrollPane();
        TilePane tilePane = new TilePane(5, 5);
        center.setContent(tilePane);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.prefWidthProperty().bind(center.widthProperty());
        tilePane.prefHeightProperty().bind(center.heightProperty().multiply(0.95));
        LinkedHashMap<String, DownloadManager> downloadManagers = DownloadFactory.getInstance().getDownloadManagers();
        AtomicInteger index = new AtomicInteger();
        toggleGroup = new ToggleGroup();
        for (Map.Entry<String, DownloadManager> entry : downloadManagers.entrySet()) {
            String pluginName = entry.getValue().getPluginName();
            String svgIcon = Optional.ofNullable(entry.getValue().getSvgIcon()).orElse(DefaultConstant.DOWNLOAD);
            ToggleButton colorfulButton = ComponentUtil.createColorfulToggleButton(index.getAndIncrement(), pluginName, svgIcon);
            colorfulButton.setToggleGroup(toggleGroup);
            tilePane.getChildren().add(colorfulButton);
        }
        root.setCenter(center);
    }

    public void step3(HBox top, HBox bottom) {
        HBox center = new HBox();
        center.setStyle("-fx-alignment: center;-fx-padding: 10");
        Label descLabel = ComponentUtil.createGeneralLabel(null);
        center.getChildren().add(descLabel);
        root.setCenter(center);
        if (toggleGroup == null || toggleGroup.getSelectedToggle() == null) {
            descLabel.setText("请先选择一个下载器");
            Button nextStep = (Button) bottom.getChildren().getLast();
            nextStep.setVisible(false);
            return;
        }
        Label titleLabel = (Label) top.getChildren().getFirst();
        titleLabel.setText("下载器介绍");
        ToggleButton selected = (ToggleButton) toggleGroup.getSelectedToggle();
        String pluginName = selected.getText();
        DownloadManager downloadManager = DownloadFactory.getInstance().getManager(pluginName);
        descLabel.setText(Optional.ofNullable(downloadManager.getPluginDesc()).orElse(downloadManager.getPluginName()));
    }

    public void step4(HBox top) {
        Label titleLabel = (Label) top.getChildren().getFirst();
        titleLabel.setText("下载器配置");

        if (applicationProperties == null) {
            InitializeService initializeService = ClientManager.getInstance().getInitializeService();
            applicationProperties = initializeService.getDefaultProperties();
        }

        ScrollPane center = ComponentUtil.createScrollPane();
        VBox vBox = new VBox();
        center.setContent(vBox);
        vBox.setStyle("-fx-alignment: center;-fx-spacing: 5;-fx-padding: 10");

        ToggleButton selected = (ToggleButton) toggleGroup.getSelectedToggle();
        String pluginName = selected.getText();
        DownloadManager downloadManager = DownloadFactory.getInstance().getManager(pluginName);
        // 获取需要的配置
        List<String> requiredProperties = Optional.ofNullable(downloadManager.getRequiredProperties()).orElse(Collections.emptyList());
        for (String requiredProperty : requiredProperties) {
            DownloadPropertiesType downloadPropertiesType = DownloadPropertiesType.ofFieldName(requiredProperty);
            if (DownloadPropertiesType.UNKNOWN.equals(downloadPropertiesType)) {
                continue;
            }
            TextInput textInput = new TextInput(downloadPropertiesType.getTitle());
            String value = downloadPropertiesType.getGetter().apply(applicationProperties.getDownloader());
            TextField textField = textInput.getItem();
            textField.setText(value);
            if (DownloadPropertiesType.DOWNLOADER_PLUGIN_NAME.equals(downloadPropertiesType)) {
                textField.setEditable(false);
            }
            vBox.getChildren().add(textInput);
            textField.textProperty().addListener((observable, oldValue, newValue) ->
                    downloadPropertiesType.getSetter().accept(applicationProperties.getDownloader(), newValue));
        }
        root.setCenter(center);
    }

    public void step5(HBox top, HBox bottom) {
        ToggleButton selected = (ToggleButton) toggleGroup.getSelectedToggle();
        String pluginName = selected.getText();
        DownloadManager downloadManager = DownloadFactory.getInstance().getManager(pluginName);
        // 获取需要的配置
        List<String> requiredProperties = Optional.ofNullable(downloadManager.getRequiredProperties()).orElse(Collections.emptyList());
        List<String> nullableProperties = Optional.ofNullable(downloadManager.getNullableProperties()).orElse(Collections.emptyList());
        StringBuilder errMsg = new StringBuilder();
        for (String requiredProperty : requiredProperties) {
            if (nullableProperties.contains(requiredProperty)) {
                continue;
            }
            DownloadPropertiesType downloadPropertiesType = DownloadPropertiesType.ofFieldName(requiredProperty);
            if (DownloadPropertiesType.UNKNOWN.equals(downloadPropertiesType)) {
                continue;
            }
            if (CommonUtil.isEmpty(downloadPropertiesType.getGetter().apply(applicationProperties.getDownloader()))) {
                errMsg.append(downloadPropertiesType.getTitle()).append("不能为空\n");
            }
        }
        Button nextStep = (Button) bottom.getChildren().getLast();
        VBox center = new VBox();
        center.setStyle("-fx-alignment: center");
        Label msgLabel = ComponentUtil.createGeneralLabel(null);
        center.getChildren().add(msgLabel);
        if (errMsg.isEmpty()) {
            errMsg.append("恭喜你完成了配置");
            nextStep.setText("完成");
            ClientManager.getInstance().setPrepareProperties(applicationProperties);
        } else {
            nextStep.setVisible(false);
        }
        msgLabel.setText(errMsg.toString());
        root.setCenter(center);
    }
}
