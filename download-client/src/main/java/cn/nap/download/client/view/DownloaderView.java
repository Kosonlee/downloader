package cn.nap.download.client.view;

import cn.nap.download.client.component.ConfirmBox;
import cn.nap.download.client.component.FileSelector;
import cn.nap.download.client.component.ProgressLoading;
import cn.nap.download.client.manager.ClientManager;
import cn.nap.download.client.util.ComponentUtil;
import cn.nap.download.client.util.ViewUtil;
import cn.nap.download.common.constant.DownloadStatus;
import cn.nap.download.common.download.DownloadManager;
import cn.nap.download.common.factory.DownloadFactory;
import cn.nap.download.common.pojo.DownloadInfo;
import cn.nap.download.common.properties.ApplicationProperties;
import cn.nap.download.common.util.CommonUtil;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DownloaderView extends AbstractView {
    private final DownloadManager downloadManager;
    private final ApplicationProperties applicationProperties;

    public DownloaderView(Stage primaryStage, BorderPane root) {
        super(primaryStage, root);
        applicationProperties = ClientManager.getInstance().getApplicationProperties();
        downloadManager = DownloadFactory.getInstance().getManager(applicationProperties.getDownloader().getPluginName());
    }

    public void initUI() {
        VBox center = new VBox();
        center.setStyle("-fx-spacing: 10;-fx-alignment: center;-fx-padding: 10");
        HBox countHBox = new HBox();
        countHBox.setStyle("-fx-spacing: 5;-fx-alignment: center");
        Label percentTitleLabel = ComponentUtil.createTitleLabel("下载进度：");
        Label percentValueLabel = ComponentUtil.createTitleLabel("0");
        Label percentUnitLabel = ComponentUtil.createTitleLabel("%");
        countHBox.getChildren().addAll(percentTitleLabel, percentValueLabel, percentUnitLabel);

        HBox speedHBox = new HBox();
        speedHBox.setStyle("-fx-spacing: 5;-fx-alignment: center");
        Label speedTitleLabel = ComponentUtil.createTitleLabel("下载速度：");
        Label speedValueLabel = ComponentUtil.createTitleLabel("0");
        Label speedUnitLabel = ComponentUtil.createTitleLabel("MB/s");
        speedHBox.getChildren().addAll(speedTitleLabel, speedValueLabel, speedUnitLabel);

        HBox statusHBox = new HBox();
        statusHBox.setStyle("-fx-spacing: 5;-fx-alignment: center");
        Label statusTitleLabel = ComponentUtil.createTitleLabel("下载状态：");
        Label statusValueLabel = ComponentUtil.createTitleLabel(DownloadStatus.INIT.getDesc());
        statusHBox.getChildren().addAll(statusTitleLabel, statusValueLabel);

        center.getChildren().addAll(countHBox, speedHBox, statusHBox);

        HBox bottom = new HBox();
        bottom.setStyle("-fx-spacing: 20;-fx-alignment: center;-fx-padding: 20");
        Button detailButton = ComponentUtil.createBlueButton("查看下载详情");
        Button downloadButton = ComponentUtil.createBlueButton("开始下载");
        bottom.getChildren().addAll(detailButton, downloadButton);
        AtomicLong lastSize = new AtomicLong();
        AtomicLong lastTimestamp = new AtomicLong();

        detailButton.setOnAction(event -> {

        });
        downloadButton.setOnAction(event -> {
            if (CommonUtil.isEmpty(applicationProperties.getDownloader().getTargetDir())) {
                ConfirmBox.yes(primaryStage, "首次启动，你需要选择下载目录");
                File file = FileSelector.selectDir(primaryStage);
                if (file == null) {
                    return;
                }
                applicationProperties.getDownloader().setTargetDir(file.getAbsolutePath());
                ClientManager.getInstance().setPrepareProperties(applicationProperties);
                ClientManager.getInstance().saveProperties();
            }
            ProgressLoading loading = new ProgressLoading(primaryStage);
            loading.add("正在初始化下载信息...", () -> {
                downloadManager.prepare(applicationProperties);
                DownloadInfo downloadInfo = downloadManager.getDownloadInfo(applicationProperties);
                if (DownloadStatus.FAILED.getStatus().equals(downloadInfo.getStatus())) {
                    log.error(downloadInfo.getErrMsg());
                }
            });
            loading.add("开始下载...", () -> onDownload(downloadButton, percentValueLabel, speedValueLabel,
                    statusValueLabel, lastSize, lastTimestamp));
            loading.execute();
        });

        root.setCenter(center);
        root.setBottom(bottom);
    }

    public void onDownload(Button downloadButton, Label percentValueLabel, Label speedValueLabel, Label statusValueLabel,
                           AtomicLong lastSize, AtomicLong lastTimestamp) {
        ViewUtil.runAndWait(() -> downloadButton.setDisable(true));
        downloadManager.download(applicationProperties);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        lastTimestamp.set(System.currentTimeMillis());
        executorService.schedule(() -> {
            DownloadInfo downloadInfo = downloadManager.getDownloadInfo(applicationProperties);
            // 百分比
            BigDecimal downloadedCount = new BigDecimal(String.valueOf(downloadInfo.getDownloadedCount().get()));
            BigDecimal totalCount = new BigDecimal(String.valueOf(downloadInfo.getTotalCount()));
            // 速度
            BigDecimal downloadedSize = new BigDecimal(String.valueOf(downloadInfo.getDownloadedSize().get()));
            BigDecimal lastSizeValue = new BigDecimal(String.valueOf(lastSize.get()));
            BigDecimal costTime = new BigDecimal(String.valueOf(System.currentTimeMillis() - lastTimestamp.get()));
            // 状态
            String status = DownloadStatus.ofStatus(downloadInfo.getStatus()).getDesc();
            // 单位
            BigDecimal MB = new BigDecimal(String.valueOf(1024 * 1024));
            BigDecimal PERCENT = new BigDecimal("100");
            if (DownloadStatus.RUNNING.getStatus().equals(downloadInfo.getStatus())) {
                ViewUtil.runAndWait(() -> {
                    percentValueLabel.setText(downloadedCount.divide(totalCount, 2, RoundingMode.FLOOR).multiply(PERCENT).toString());
                    speedValueLabel.setText(downloadedSize.subtract(lastSizeValue)
                            .divide(MB, 2, RoundingMode.FLOOR)
                            .divide(costTime, 2, RoundingMode.FLOOR)
                            .toString());
                    lastSize.set(downloadInfo.getDownloadedSize().get());
                    lastTimestamp.set(System.currentTimeMillis());
                    statusValueLabel.setText(status);
                });
                return;
            }
            // 结束重置
            ViewUtil.runAndWait(() -> {
                downloadButton.setDisable(false);
                downloadButton.setText("重新下载");
                percentValueLabel.setText(downloadedCount.divide(totalCount, 2, RoundingMode.FLOOR).multiply(PERCENT).toString());
                speedValueLabel.setText(downloadedSize.subtract(lastSizeValue)
                        .divide(MB, 2, RoundingMode.FLOOR)
                        .divide(costTime, 2, RoundingMode.FLOOR)
                        .toString());
                statusValueLabel.setText(status);
            });
            lastSize.set(0);
            executorService.shutdown();
        }, 1, TimeUnit.SECONDS);
    }
}
