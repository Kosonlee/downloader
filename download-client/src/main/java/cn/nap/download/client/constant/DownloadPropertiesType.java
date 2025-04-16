package cn.nap.download.client.constant;

import cn.nap.download.common.properties.DownloaderProperties;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 参数最好用String接收
 * 如果配置类型不是String，需要做类型转换
 * 如getter: downloadProperties -> String.valueOf(downloadProperties.getInt())
 * 如setter: (downloaderProperties, type) -> downloaderProperties.setInt(Integer.valueOf(type))
 */
@Getter
public enum DownloadPropertiesType {
    DOWNLOADER_PLUGIN_NAME("pluginName", "下载器类型", DownloaderProperties::getPluginName, DownloaderProperties::setPluginName),
    DOWNLOADER_SERVER_URL("downloadUrl", "下载地址", DownloaderProperties::getDownloadUrl, DownloaderProperties::setDownloadUrl),
    DOWNLOAD_PASSWD("passwd", "密码、验证码", DownloaderProperties::getPasswd, DownloaderProperties::setPasswd),
    DOWNLOAD_TARGET_DIR("targetDir", "存放目录", DownloaderProperties::getTargetDir, DownloaderProperties::setTargetDir),
    UNKNOWN("unknown", "unknown", null, null);

    private final String fieldName;
    private final String title;
    private final Function<DownloaderProperties, String> getter;
    private final BiConsumer<DownloaderProperties, String> setter;

    DownloadPropertiesType(String fieldName, String title, Function<DownloaderProperties, String> getter, BiConsumer<DownloaderProperties, String> setter) {
        this.fieldName = fieldName;
        this.title = title;
        this.getter = getter;
        this.setter = setter;
    }

    public static DownloadPropertiesType ofFieldName(String fieldName) {
        for (DownloadPropertiesType value : values()) {
            if (value.fieldName.equals(fieldName)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
