package cn.nap.download.client.component;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileSelector {

    public static File selectDir(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择下载目录");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
        return directoryChooser.showDialog(primaryStage);
    }
}
