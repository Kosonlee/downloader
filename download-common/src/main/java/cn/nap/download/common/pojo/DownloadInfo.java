package cn.nap.download.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadInfo {
    /**
     * 下载状态
     *
     * @see cn.nap.download.common.constant.DownloadStatus
     */
    private String status;
    /**
     * 总文件数
     */
    private int totalCount;
    /**
     * 总文件大小
     */
    private long totalSize;
    /**
     * 已下载总文件数
     */
    private AtomicInteger downloadedCount;
    /**
     * 已下载总文件大小
     */
    private AtomicLong downloadedSize;
    /**
     * 失败原因
     */
    private String errMsg;
}
