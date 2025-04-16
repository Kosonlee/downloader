package cn.nap.download.client.manager;

import cn.nap.download.client.service.InitializeService;
import cn.nap.download.common.properties.ApplicationProperties;
import lombok.Data;

@Data
public class ClientManager {
    private static ClientManager instance;
    private final InitializeService initializeService;
    private ApplicationProperties applicationProperties;
    private ApplicationProperties prepareProperties;

    public static ClientManager getInstance() {
        if (instance == null) {
            instance = new ClientManager();
        }
        return instance;
    }

    private ClientManager() {
        this.initializeService = new InitializeService();
        this.applicationProperties = initializeService.initProperties();
    }

    public void saveProperties() {
        initializeService.generatePropertiesFile(prepareProperties);
        applicationProperties = initializeService.initProperties();
    }
}
