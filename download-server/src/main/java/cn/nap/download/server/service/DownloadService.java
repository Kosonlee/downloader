package cn.nap.download.server.service;

import cn.nap.download.common.util.CommonUtil;
import cn.nap.download.common.util.SecUtil;
import cn.nap.download.server.pojo.ListResp;
import cn.nap.download.server.properties.DownloaderProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class DownloadService {
    private final DownloaderProperties downloaderProperties;
    private String root;
    private final Map<String, Pair<FileTime, String>> cache = new HashMap<>();

    public DownloadService(DownloaderProperties downloaderProperties) {
        this.downloaderProperties = downloaderProperties;
    }

    public List<ListResp> list() {
        Path downloadDir = Paths.get(downloaderProperties.getDir()).toAbsolutePath().normalize();
        root = downloadDir.toString();
        List<ListResp> result = new ArrayList<>();
        listFiles(downloadDir, result);
        return result;
    }

    public ResponseEntity<Resource> download(String path) throws Exception {
        CommonUtil.nonNullElseThrow(path, "path不能为空");
        // 修复目录穿越的漏洞
        if (path.contains("..")) {
            throw new IllegalArgumentException("禁止目录穿越");
        }
        Path downloadDir = Paths.get(downloaderProperties.getDir()).toAbsolutePath().normalize();
        Path file = downloadDir.resolve(path).normalize();
        if (!Files.exists(file)) {
            throw new FileNotFoundException();
        }
        // 识别不到文件类型时，设置为application/octet-stream
        String contentType;
        try {
            contentType = Optional.ofNullable(Files.probeContentType(file)).orElse("application/octet-stream");
        } catch (Exception e) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(new UrlResource(file.toUri()));
    }

    private void listFiles(Path downloadDir, List<ListResp> result) {
        if (!Files.exists(downloadDir)) {
            try {
                Files.createDirectories(downloadDir);
            } catch (IOException e) {
                log.error("创建目录失败", e);
            }
            return;
        }
        if (Files.isDirectory(downloadDir)) {
            try (Stream<Path> stream = Files.list(downloadDir)) {
                stream.forEach(path -> listFiles(path, result));
            } catch (IOException e) {
                log.error("获取文件列表错误：{}", e.getMessage(), e);
                result.clear();
            }
        } else {
            // 只有一级目录
            String path;
            if (downloadDir.getParent().toString().length() == root.length()) {
                path = "./";
            } else {
                path = downloadDir.getParent().toString().substring(root.length() + 1).replaceAll("\\\\", "/");
            }
            try {
                String md5;
                String absolutePath = downloadDir.toFile().getAbsolutePath();
                FileTime lastModifiedTime = Files.getLastModifiedTime(downloadDir);
                // 缓存避免重复计算md5
                if (cache.containsKey(absolutePath) && cache.get(absolutePath).getLeft().equals(lastModifiedTime)) {
                    md5 = cache.get(absolutePath).getRight();
                } else {
                    md5 = SecUtil.md5("nap", downloadDir.toFile());
                    cache.put(downloadDir.toFile().getAbsolutePath(), Pair.of(lastModifiedTime, md5));
                }

                result.add(ListResp.builder()
                        .name(downloadDir.getFileName().toString())
                        .md5(md5)
                        .path(path)
                        .size(Files.size(downloadDir))
                        .build());
            } catch (IOException e) {
                log.error("获取文件大小异常：{}", e.getMessage(), e);
                result.clear();
            }
        }
    }
}
