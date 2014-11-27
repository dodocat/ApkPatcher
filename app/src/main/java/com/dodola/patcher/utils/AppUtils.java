package com.dodola.patcher.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 应用程序的工具类
 *
 */
public class AppUtils {

    /**
     * 安装一个应用程序
     *
     * @param context
     * @param apkfile
     */
    public static void installApplication(Context context, File apkfile) {
      /*   <action android:name="android.intent.action.VIEW" />
         <category android:name="android.intent.category.DEFAULT" />
         <data android:scheme="content" />
         <data android:scheme="file" />
         <data android:mimeType="application/vnd.android.package-archive" />*/
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
//		intent.setType("application/vnd.android.package-archive");
//		intent.setData(Uri.fromFile(apkfile));
        intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
