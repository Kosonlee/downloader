package cn.nap.download.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "downloader")
@Data
public class DownloaderProperties {
    private String dir;
}
