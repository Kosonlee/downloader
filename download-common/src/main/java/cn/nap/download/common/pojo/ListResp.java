package cn.nap.download.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListResp {
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件md5值
     */
    private String md5;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件大小
     */
    private long size;
}
