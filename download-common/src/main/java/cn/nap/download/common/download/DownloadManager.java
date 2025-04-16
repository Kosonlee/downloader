package cn.nap.download.common.download;

import cn.nap.download.common.pojo.DownloadInfo;
import cn.nap.download.common.properties.ApplicationProperties;

import java.util.List;

public interface DownloadManager {
    /**
     * @return 获取插件名
     */
    String getPluginName();

    /**
     * @return 获取插件描述，没有则取
     */
    String getPluginDesc();

    /**
     * @return 获取插件svg图标，没有取默认图标
     */
    String getSvgIcon();

    /**
     * 因为支持插件，所以存在一些配置是给其他插件使用的，所以这里只需要获取自己需要的配置
     * 注意这里用的Arrays.asList，所以不能更改
     * 参数最好用String接收
     * 如果配置类型不是String，需要做类型转换
     * 如getter: downloadProperties -> String.valueOf(downloadProperties.getInt())
     * 如setter: (downloaderProperties, type) -> downloaderProperties.setInt(Integer.valueOf(type))
     *
     * @return 获取需要的配置
     */
    List<String> getRequiredProperties();

    /**
     * 上面定义了需要的配置，这定义哪些配置可以为空，为空的配置不会进行校验
     *
     * @return 获取允许为空的配置
     */
    List<String> getNullableProperties();

    /**
     * 下载前准备
     */
    void prepare(ApplicationProperties applicationProperties);

    /**
     * 下载操作
     */
    void download(ApplicationProperties applicationProperties);

    /**
     * @return 下载信息
     */
    DownloadInfo getDownloadInfo(ApplicationProperties applicationProperties);
}
