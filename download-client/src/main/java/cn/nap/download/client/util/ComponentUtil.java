package cn.nap.download.client.util;

import cn.nap.download.client.component.ViewStyle;
import cn.nap.download.client.constant.ViewConstant;
import cn.nap.download.common.util.CommonUtil;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class ComponentUtil {
    public static Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18px;-fx-font-weight: bold;-fx-text-fill: white");
        return label;
    }

    public static Label createLinearGradientLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;-fx-font-weight: bold;-fx-text-fill: linear-gradient(from 0% 0% to 100% 100%, blueviolet, deeppink)");
        return label;
    }

    public static Label createGeneralLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;-fx-font-weight: bold;-fx-text-fill: white");
        return label;
    }

    public static Button createBlueButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-text-fill: white;-fx-background-color: #036EC1; -fx-font-size: 14px;");
        return button;
    }

    public static Button createGeneralButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-text-fill: white;-fx-background-color: transparent;-fx-font-size: 14px;-fx-border-radius: 5;-fx-border-color: white;-fx-border-width: 1px");
        return button;
    }

    public static ToggleButton createColorfulToggleButton(int index, String text, String shape) {
        ToggleButton button = new ToggleButton(text);
        Region region = createSvgRegion(shape);
        String popularColor = ViewConstant.POPULAR_COLORS[index % ViewConstant.POPULAR_COLORS.length];
        region.setStyle("-fx-background-color: " + popularColor + ";-fx-pref-width: 25;-fx-pref-height: 25;-fx-max-width: 25;-fx-max-height: 25");
        button.setGraphic(region);
        button.setStyle("-fx-pref-width: 120;-fx-min-height: 70;-fx-content-display: top;-fx-graphic-text-gap: 10;-fx-font-weight: bold;-fx-background-color: transparent;-fx-background-radius: 10;-fx-border-width: 2;-fx-border-color: transparent;-fx-border-radius: 10;-fx-text-fill: white");
        ViewStyle viewStyle = new ViewStyle(button);
        button.setOnMouseEntered(event -> viewStyle.updateAndSet("-fx-background-color: gray"));
        button.setOnMouseExited(event -> viewStyle.updateAndSet("-fx-background-color: transparent"));
        button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                viewStyle.updateAndSet("-fx-border-color: " + popularColor);
            } else {
                viewStyle.updateAndSet("-fx-border-color: transparent");
            }
        });
        return button;
    }

    public static <T> ComboBox<T> createComboBox(String promptText) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setStyle("-fx-background-color: null;-fx-border-width: 1;-fx-border-color: gray;-fx-border-radius: 5;-fx-text-fill: white");
        return comboBox;
    }

    public static <T> ComboBox<T> createComboBox(String promptText, T... ts) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setPromptText(promptText);
        comboBox.setStyle("-fx-background-color: null;-fx-border-width: 1;-fx-border-color: gray;-fx-border-radius: 5");
        if (!CommonUtil.isEmpty(ts)) {
            comboBox.getItems().addAll(ts);
        }
        return comboBox;
    }

    public static Region createSvgRegion(String style) {
        return createSvgRegion(style, "black", 14);
    }

    public static Region createSvgRegion(String style, String color) {
        return createSvgRegion(style, color, 14);
    }

    public static Region createSvgRegion(String style, int size) {
        return createSvgRegion(style, "black", size);
    }

    public static Region createSvgRegion(String style, String color, int size) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(style);
        Region region = new Region();
        region.setShape(svgPath);
        region.setStyle("-fx-background-color:" + color);
        region.setPrefSize(size, size);
        region.setMaxSize(size, size);
        return region;
    }

    public static ScrollPane createScrollPane() {
        ScrollPane center = new ScrollPane();
        center.setStyle("-fx-background: rgb(31, 31, 31);-fx-hbar-policy: never");
        return center;
    }
}
