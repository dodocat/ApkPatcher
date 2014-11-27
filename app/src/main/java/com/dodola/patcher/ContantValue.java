package com.dodola.patcher;

public interface ContantValue {
    /**
     * 字符编码
     */
    public static String ENCODE = "UTF-8";
    /**
     * 服务端的的URL
     */
    public static String BASE_URL = "http://10.0.2.2:8080/UpApk";

    /**
     * 检查版本号是否为最新
     */
    String CHECK_VERSION_CODE_URL = BASE_URL + "/CheckVersionServlet";

    /**
     * 获取差异包的URL地址
     */
    String UPGRADE_URL = BASE_URL + "/UpGradeServlet";

}

