package cn.nap.download.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadDetail {
    /**
     * 下载状态
     * @see cn.nap.download.common.constant.DownloadStatus
     */
    private String status;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 存放路径，不包含文件名
     */
    private String filePath;
    /**
     * 文件大小
     */
    private long maxSize;
    /**
     * 已下载大小
     */
    private AtomicLong downloadedSize;
    /**
     * 文件md5
     */
    private String md5;
}
