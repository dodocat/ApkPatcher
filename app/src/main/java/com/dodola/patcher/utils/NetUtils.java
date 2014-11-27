package com.dodola.patcher.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.dodola.patcher.GloableParams;

public class NetUtils {
    private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");// 4.0模拟器屏蔽掉该权限

    /**
     * 检查网络
     *
     * @return
     */
    public static boolean checkNetWork(Context context) {
        // ConnectivityManager//系统服务
        // ①判断WIFI联网情况
        boolean isWifi = isWifi(context);
        // ②判断MOBILE联网情况
        boolean isMobile = isMobile(context);

        if (!isWifi && !isMobile) {
            // 如果都不能联网，提示用户
            return false;
        }

        // ③判断MOBILE是否连接
        if (isMobile) {
            // 如果是，判断一下是否是wap（代理的信息）
            // wap 还是 net？看当前正在连接的去渠道的配置信息（proxy port），如果有值wap
            readAPN(context);
        }
        return true;
    }

    public static String readAPN(Context context) {
        Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI,
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            GloableParams.PROXY_IP = cursor.getString(cursor
                    .getColumnIndex("proxy"));
            GloableParams.PROXY_PORT = cursor.getInt(cursor
                    .getColumnIndex("port"));
        }
        return "";
    }

    /**
     * 判断Mobile是否处于连接状态
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo.isConnected();
    }

    /**
     * 判断wifi是否处于连接状态
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }
}
