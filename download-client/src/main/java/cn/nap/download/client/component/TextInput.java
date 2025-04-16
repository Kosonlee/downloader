package cn.nap.download.client.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class TextInput extends HBox {
    private final Label text;
    private final TextField item;
    public TextInput(String labelText) {
        setSpacing(5);
        setPadding(new Insets(5, 0, 5, 0));
        setAlignment(Pos.CENTER);
        text = new Label(labelText);
        text.setStyle("-fx-font-size: 14px;-fx-pref-width: 120;-fx-text-fill: white");
        item = new TextField();
        getChildren().addAll(text, item);
    }

    public Label getText() {
        return text;
    }

    public TextField getItem() {
        return item;
    }
}
