package cn.nap.download.common.factory;

import cn.nap.download.common.download.DownloadManager;
import cn.nap.download.common.major.MajorManager;
import cn.nap.download.common.util.CommonUtil;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.jar.JarFile;

public class DownloadFactory {
    private static DownloadFactory instance;
    private final LinkedHashMap<String, DownloadManager> downloadManagers;

    private DownloadFactory() {
        downloadManagers = new LinkedHashMap<>();
        scanManager();
    }

    public static DownloadFactory getInstance() {
        if (instance == null) {
            instance = new DownloadFactory();
        }
        return instance;
    }

    private void scanManager() {
        // 注册默认的manager
        MajorManager majorManager = new MajorManager();
        downloadManagers.put(majorManager.getPluginName(), majorManager);

        // 读取plugins文件夹下的所有jar包
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL pluginsURL = classLoader.getResource("/plugins");
        // 没有插件目录
        if (pluginsURL == null) {
            return;
        }
        try {
            File pluginsDir = new File(pluginsURL.toURI());
            File[] files = pluginsDir.listFiles();
            if (CommonUtil.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                // 非jar文件不加载
                if (!file.getName().endsWith(".jar")) {
                    continue;
                }
                JarFile jarFile = new JarFile(file);
                jarFile.stream().forEach(jarEntry -> {
                    String entryName = jarEntry.getName();
                    // 非class文件不处理
                    if (!entryName.endsWith(".class")) {
                        return;
                    }
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                    addContainer(classLoader, className);
                });
                jarFile.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("扫描插件发生异常", e);
        }
    }

    public DownloadManager getManager(String pluginName) {
        return downloadManagers.get(pluginName);
    }

    public LinkedHashMap<String, DownloadManager> getDownloadManagers() {
        return downloadManagers;
    }

    private void addContainer(ClassLoader classLoader, String className) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            if (!DownloadManager.class.isAssignableFrom(clazz)) {
                return;
            }
            DownloadManager manager = (DownloadManager) clazz.getDeclaredConstructor().newInstance();
            if (CommonUtil.isEmpty(manager.getPluginName())) {
                return;
            }
            downloadManagers.put(manager.getPluginName(), manager);
        } catch (Exception ignore) {

        }
    }
}
