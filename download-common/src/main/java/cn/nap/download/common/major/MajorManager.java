package cn.nap.download.common.major;

import cn.nap.download.common.constant.DefaultConstant;
import cn.nap.download.common.constant.DownloadStatus;
import cn.nap.download.common.download.DownloadManager;
import cn.nap.download.common.pojo.DownloadDetail;
import cn.nap.download.common.pojo.DownloadInfo;
import cn.nap.download.common.pojo.ListResp;
import cn.nap.download.common.properties.ApplicationProperties;
import cn.nap.download.common.properties.DownloaderProperties;
import cn.nap.download.common.util.CommonUtil;
import cn.nap.download.common.util.SecUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 默认manager，类似于DefaultManager，default是java关键字，所以用major
 */
public class MajorManager implements DownloadManager {
    private final DownloadInfo downloadInfo = new DownloadInfo();
    private final List<DownloadDetail> downloadDetails = new ArrayList<>();
    private Semaphore semaphore;
    private ExecutorService executorService;

    @Override
    public String getPluginName() {
        return DefaultConstant.PLUGIN_NAME;
    }

    @Override
    public String getPluginDesc() {
        return "基础下载器，配套服务端使用";
    }

    @Override
    public String getSvgIcon() {
        return DefaultConstant.DOWNLOAD;
    }

    @Override
    public List<String> getRequiredProperties() {
        return Arrays.asList("pluginName", "downloadUrl");
    }

    @Override
    public List<String> getNullableProperties() {
        return List.of();
    }

    @Override
    public void prepare(ApplicationProperties applicationProperties) {
        downloadInfo.setStatus(DownloadStatus.INIT.getStatus());
        downloadInfo.setTotalCount(0);
        downloadInfo.setTotalSize(0);
        downloadInfo.setDownloadedCount(new AtomicInteger());
        downloadInfo.setDownloadedSize(new AtomicLong());
        downloadInfo.setErrMsg(null);
        downloadDetails.clear();
        try {
            DownloaderProperties downloader = applicationProperties.getDownloader();
            String url = downloader.getDownloadUrl() + "/download/v1/list";
            System.out.println("url: " + url);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                downloadInfo.setStatus(DownloadStatus.FAILED.getStatus());
                downloadInfo.setErrMsg("下载失败，状态码：" + httpResponse.statusCode());
                return;
            }
            System.out.println("response: " + httpResponse.body());
            List<ListResp> listRespList = JSONObject.parseObject(httpResponse.body(), new TypeReference<>() {
            });
            for (ListResp listResp : listRespList) {
                downloadDetails.add(DownloadDetail.builder()
                        .status(DownloadStatus.INIT.getStatus())
                        .fileName(listResp.getName())
                        .filePath(listResp.getPath())
                        .maxSize(listResp.getSize())
                        .downloadedSize(new AtomicLong())
                        .md5(listResp.getMd5())
                        .build());
                downloadInfo.setTotalCount(downloadInfo.getTotalCount() + 1);
                downloadInfo.setTotalSize(downloadInfo.getTotalSize() + listResp.getSize());
            }
        } catch (Exception e) {
            downloadInfo.setStatus(DownloadStatus.FAILED.getStatus());
            downloadInfo.setErrMsg(e.getMessage());
            downloadDetails.clear();
            return;
        }
        // 预留这里，万一后面支持配置线程数
        semaphore = new Semaphore(3);
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public void download(ApplicationProperties applicationProperties) {
        for (DownloadDetail downloadDetail : downloadDetails) {
            executorService.submit(() -> {
                if (DownloadStatus.FAILED.getStatus().equals(downloadDetail.getStatus())) {
                    return;
                }
                try {
                    semaphore.acquire();
                    // 这里被并发应该没有问题
                    if (DownloadStatus.INIT.getStatus().equals(downloadDetail.getStatus())) {
                        downloadInfo.setStatus(DownloadStatus.RUNNING.getStatus());
                    }

                    DownloaderProperties downloader = applicationProperties.getDownloader();
                    Path dir = Path.of(downloader.getTargetDir(), downloadDetail.getFilePath());
                    if (Files.notExists(dir)) {
                        try {
                            Files.createDirectories(dir);
                        } catch (Exception ignore) {

                        }
                    }
                    Path file = Path.of(downloader.getTargetDir(), downloadDetail.getFilePath(), downloadDetail.getFileName());
                    if (Files.exists(file)) {
                        String md5 = SecUtil.md5("nap", file.toFile());
                        if (Objects.equals(md5, downloadDetail.getMd5())) {
                            long size = Files.size(file);
                            downloadDetail.getDownloadedSize().addAndGet(size);
                            downloadInfo.getDownloadedCount().incrementAndGet();
                            downloadInfo.getDownloadedSize().addAndGet(size);
                            return;
                        }
                    }
                    String url = downloader.getDownloadUrl() + "/download/v1/download?path=" + downloadDetail.getFilePath() + "/" + downloadDetail.getFileName();
                    System.out.println("url: " + url);
                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest httpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();
                    HttpResponse<InputStream> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
                    if (httpResponse.statusCode() != 200) {
                        downloadInfo.setStatus(DownloadStatus.FAILED.getStatus());
                        downloadInfo.setErrMsg("下载失败，状态码：" + httpResponse.statusCode());
                        return;
                    }
                    MessageDigest md5Digest = SecUtil.createMd5SaltDigest("nap");
                    try (InputStream is = httpResponse.body();
                         OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            md5Digest.update(buffer, 0, len);
                            os.write(buffer, 0, len);
                        }
                        StringBuilder md5Code = new StringBuilder();
                        md5Code.append(new BigInteger(1, md5Digest.digest()).toString(16));
                        // 不足32位补0
                        CommonUtil.leftPad(md5Code, 32, '0');
                        if (md5Code.toString().equals(downloadDetail.getMd5())) {
                            downloadDetail.getDownloadedSize().addAndGet(downloadDetail.getMaxSize());
                            downloadInfo.getDownloadedCount().incrementAndGet();
                            downloadInfo.getDownloadedSize().addAndGet(downloadDetail.getMaxSize());
                            return;
                        }
                    }
                    // md5不匹配
                    Files.deleteIfExists(file);
                    throw new RuntimeException("下载文件md5校验失败");
                } catch (Exception e) {
                    downloadInfo.setStatus(DownloadStatus.FAILED.getStatus());
                    downloadInfo.setErrMsg(e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (DownloadStatus.RUNNING.getStatus().equals(downloadInfo.getStatus())) {
                        if (downloadInfo.getDownloadedCount().get() == downloadInfo.getTotalCount()
                                && downloadInfo.getDownloadedSize().get() == downloadInfo.getTotalSize()) {
                            downloadInfo.setStatus(DownloadStatus.FINISHED.getStatus());
                            executorService.shutdown();
                        }
                    } else if (DownloadStatus.FAILED.getStatus().equals(downloadInfo.getStatus())) {
                        executorService.shutdown();
                    }
                    semaphore.release();
                }
            });

        }
    }

    @Override
    public DownloadInfo getDownloadInfo(ApplicationProperties applicationProperties) {
        return downloadInfo;
    }
}
