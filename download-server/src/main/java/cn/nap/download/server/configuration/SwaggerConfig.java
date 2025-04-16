package cn.nap.download.server.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("nap-download-server")
                        .description("nap-downloader 的默认服务端，可以自定义自己的服务端，用于下载文件")
                        .version("v1"));
    }
}
