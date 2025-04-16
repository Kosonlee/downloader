package cn.nap.download.client.service;

import cn.nap.download.common.constant.DefaultConstant;
import cn.nap.download.common.properties.ApplicationProperties;
import cn.nap.download.common.properties.DownloaderProperties;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class InitializeService {
    private final ClassLoader CLASS_LOADER = this.getClass().getClassLoader();

    public ApplicationProperties initProperties() {
        InputStream ymlStream = CLASS_LOADER.getResourceAsStream("application.yml");
        if (ymlStream == null) {
            return null;
        }
        Yaml yaml = new Yaml();
        // 读取 YAML 文件
        return yaml.loadAs(ymlStream, ApplicationProperties.class);
    }

    public void generatePropertiesFile(ApplicationProperties applicationProperties) {
        URL resource = CLASS_LOADER.getResource("application.yml");
        if (resource != null) {
            try {
                generatePropertiesFile(applicationProperties, new File(resource.toURI()).getAbsolutePath());
                return;
            } catch (Exception e) {
                throw new RuntimeException("生成配置文件失败：" + e.getMessage());
            }
        }
        resource = CLASS_LOADER.getResource("log4j2.xml");
        if (resource == null) {
            throw new RuntimeException("resources资源路径不存在，无法生成配置");
        }
        try {
            String path = new File(resource.toURI()).getParent() + "/application.yml";
            generatePropertiesFile(applicationProperties, path);
        } catch (Exception e) {
            throw new RuntimeException("生成配置文件失败：" + e.getMessage());
        }
    }

    public void generatePropertiesFile(ApplicationProperties applicationProperties, String path) throws IOException {
        // 配置 YAML 输出选项
        DumperOptions options = new DumperOptions();
        // 生成一行一行的 YAML 文件，而不是紧凑的 YAML 文件
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        // 创建 SnakeYAML 解析器
        Yaml yaml = new Yaml(options);
        try(FileWriter writer = new FileWriter(path)) {
            // 将对象转换为map，因为用对象会生成!!开头的显式标签
            yaml.dump(JSON.parseObject(JSONObject.toJSONString(applicationProperties)), writer);
        }
    }

    public ApplicationProperties getDefaultProperties() {
        DownloaderProperties downloaderProperties = DownloaderProperties.builder()
                .pluginName(DefaultConstant.PLUGIN_NAME)
                .downloadUrl("http://localhost:3030")
                .build();
        return ApplicationProperties.builder()
                .downloader(downloaderProperties)
                .build();
    }
}
