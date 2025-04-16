module cn.nap.download.client {
    requires java.base;
    requires java.net.http;

    requires javafx.controls;
    requires javafx.graphics;
    requires org.yaml.snakeyaml;
    requires static lombok;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires com.alibaba.fastjson2;
    requires download.common;

    opens cn.nap.download.client;
}