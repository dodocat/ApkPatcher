package com.dodola.patcher.utils;

import com.dodola.patcher.engineImpl.PatcherEngineImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 工厂类,用于层与层解耦
 *
 */
public class BeanFactory {

    /**
     * 加载指定的properties文件
     */
    private static Properties properties;

    static {
        properties = new Properties();
        properties.setProperty("PatcherEngine", "com.dodola.patcher.engineImpl.PatcherEngineImpl");

    }

    /**
     * 获取config/bean.properties对应的实现类
     *
     * @param clazz
     * @return
     */
    public static <T> T getInstance(Class<T> clazz) {

        return (T) new PatcherEngineImpl();

    }
}
