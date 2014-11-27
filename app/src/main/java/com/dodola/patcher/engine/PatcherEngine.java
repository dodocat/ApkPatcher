package com.dodola.patcher.engine;

import android.content.Context;

/**
 * 连接服务器的接口
 *
 */
public interface PatcherEngine {

    /**
     * 是否是最新版本
     *
     * @return
     */
    boolean isVersionCode(Context context);

    /**
     * 获取客户端与服务端最新版本差异包的URL地址
     *
     * @return
     */
    String getUpGrade(Context context);
}
