package cn.nap.download.server.controller;

import cn.nap.download.server.pojo.ListResp;
import cn.nap.download.server.service.DownloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/download")
@Tag(name = "下载接口")
@Slf4j
public class DownloadController {
    private final DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @Operation(summary = "下载列表")
    @GetMapping("/v1/list")
    public List<ListResp> list() {
        try {
            return downloadService.list();
        } catch (Exception e) {
            log.error("获取文件列表失败：{}", e.getMessage(), e);
            return List.of();
        }
    }

    @Operation(summary = "下载列表")
    @GetMapping("/v1/download")
    public ResponseEntity<Resource> download(@Parameter(name = "path", description = "文件名，需要包含路径") @RequestParam("path") String path) {
        try {
            return downloadService.download(path);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("未知错误：{}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
