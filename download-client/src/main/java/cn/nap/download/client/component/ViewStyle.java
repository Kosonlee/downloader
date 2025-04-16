package cn.nap.download.client.component;

import cn.nap.download.common.util.CommonUtil;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.Map;

public class ViewStyle {
    private final Region region;
    private final Map<String, String> styleMap = new HashMap<>();

    public ViewStyle(Region region) {
        this.region = region;
        resolve();
    }

    public void update(String style) {
        resolve(style);
    }

    public String get() {
        StringBuilder styleBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : styleMap.entrySet()) {
            styleBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        return styleBuilder.toString();
    }

    public void remove(String style) {
        String[] styleArr = style.split(";");
        for (String tmpStyle : styleArr) {
            String[] keyValArr = tmpStyle.split(":");
            if (keyValArr.length == 1 || keyValArr.length == 2) {
                styleMap.remove(keyValArr[0].trim());
            }
        }
    }

    public void updateAndSet(String style) {
        update(style);
        region.setStyle(get());
    }

    public void removeAndSet(String style) {
        remove(style);
        region.setStyle(get());
    }

    public void modifyAndSet(String updateStyle, String removeStyle) {
        update(updateStyle);
        remove(removeStyle);
        region.setStyle(get());
    }

    private void resolve() {
        String paneStyle = region.getStyle();
        if (CommonUtil.isEmpty(paneStyle)) {
            return;
        }
        resolve(paneStyle);
    }

    private void resolve(String style) {
        String[] styleArr = style.split(";");
        for (String tmpStyle : styleArr) {
            String[] keyValArr = tmpStyle.split(":");
            if (keyValArr.length != 2) {
                continue;
            }
            styleMap.put(keyValArr[0].trim(), keyValArr[1].trim());
        }
    }
}

