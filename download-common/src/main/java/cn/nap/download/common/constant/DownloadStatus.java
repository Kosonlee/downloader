package cn.nap.download.common.constant;

import lombok.Getter;

@Getter
public enum DownloadStatus {
    INIT("0", "未开始"),
    RUNNING("1", "下载中"),
    FINISHED("2", "已完成"),
    FAILED("3", "已失败");

    private final String status;
    private final String desc;

    DownloadStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static DownloadStatus ofStatus(String status) {
        for (DownloadStatus value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return INIT;
    }
}
