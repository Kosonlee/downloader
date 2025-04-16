package cn.nap.download.client.component;

import cn.nap.download.client.util.ViewUtil;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProgressLoading extends Stage {
    private final LinkedBlockingQueue<Pair<String, Runnable>> queue = new LinkedBlockingQueue<>();
    private final Label label;
    private final Long timeout;

    public ProgressLoading(Stage primaryStage) {
        this(primaryStage, null);
    }

    public ProgressLoading(Stage primaryStage, Long timeout) {
        this.timeout = timeout;

        VBox root = new VBox();
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        ProgressIndicator indicator = new ProgressIndicator();
        label = new Label("Loading...");
        label.setStyle("-fx-text-fill: white;-fx-font-size: 14px;-fx-font-weight: bold");
        root.getChildren().addAll(indicator, label);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setWidth(200);
        setHeight(100);
        initOwner(primaryStage);
        initModality(Modality.WINDOW_MODAL);
        ViewUtil.centerOnParent(primaryStage, this);
        show();
    }

    public synchronized void add(String text, Runnable runnable) {
        queue.offer(new Pair<>(text, runnable));
    }

    public void execute() {
        new Thread(() -> {
            Pair<String, Runnable> poll;
            while ((poll = queue.poll()) != null) {
                Pair<String, Runnable> finalPoll = poll;
                ViewUtil.runAndWait(() -> label.setText(finalPoll.getKey()));
                CompletableFuture<Void> future = null;
                try {
                    future = CompletableFuture.runAsync(() -> finalPoll.getValue().run());
                    if (timeout == null) {
                        future.get();
                    } else {
                        future.get(timeout, TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    log.error("异步任务执行异常：{}", e.getMessage(), e);
                    if (future != null) {
                        future.complete(null);
                    }
                    // 发生异常不继续执行，避免雪崩
                    break;
                }
            }
            ViewUtil.runAndWait(this::close);
        }).start();
    }
}

