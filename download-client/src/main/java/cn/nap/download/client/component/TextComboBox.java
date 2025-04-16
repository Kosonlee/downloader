package cn.nap.download.client.component;

import cn.nap.download.client.util.ComponentUtil;
import cn.nap.download.common.util.CommonUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Collection;

public class TextComboBox<T> extends HBox {
    private final Label text;
    private final ComboBox<T> item;

    public TextComboBox(String labelText, T... selectTexts) {
        setSpacing(5);
        setPadding(new Insets(5, 0, 5, 0));
        setAlignment(Pos.CENTER);
        text = ComponentUtil.createGeneralLabel(labelText);
        item = new ComboBox<>();
        if (!CommonUtil.isEmpty(selectTexts)) {
            item.getItems().addAll(selectTexts);
        }
        getChildren().addAll(text, item);
    }

    public TextComboBox(String labelText, Collection<T> selectTexts) {
        setSpacing(5);
        setPadding(new Insets(5, 0, 5, 0));
        setAlignment(Pos.CENTER);
        text = ComponentUtil.createGeneralLabel(labelText);
        item = ComponentUtil.createComboBox(null);
        if (!CommonUtil.isEmpty(selectTexts)) {
            item.getItems().addAll(selectTexts);
        }
        getChildren().addAll(text, item);
    }

    public Label getText() {
        return text;
    }

    public ComboBox<T> getItem() {
        return item;
    }
}
