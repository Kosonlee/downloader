package cn.nap.download.common.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloaderProperties implements Serializable {
    /**
     * 下载器类型
     */
    private String pluginName;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 密码、验证码
     */
    private String passwd;
    /**
     * 存放目录
     */
    private String targetDir;
}
