package cn.nap.download.server.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListResp {
    @Schema(title = "文件名")
    private String name;
    @Schema(title = "文件md5值")
    private String md5;
    @Schema(title = "文件路径")
    private String path;
    @Schema(title = "文件大小")
    private long size;
}
