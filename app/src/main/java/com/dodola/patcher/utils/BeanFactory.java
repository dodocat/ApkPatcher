package com.dodola.patcher.utils;

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
        InputStream is = BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取config/bean.properties对应的实现类
     *
     * @param clazz
     * @return
     */
    public static <T> T getInstance(Class<T> clazz) {
        String clazzName = properties.getProperty(clazz.getSimpleName());
        try {
            return (T) Class.forName(clazzName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
