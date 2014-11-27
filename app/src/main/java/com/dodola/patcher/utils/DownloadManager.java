package com.dodola.patcher.utils;

import android.app.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadManager {

    /**
     * 下载文件的操作
     *
     * @param url  文件的路径
     * @param path 保存文件的路径
     * @param pd   下载进度的对话框
     * @return 返回null下载失败
     */
    public static File download(String fileurl, String path, ProgressDialog pd) {
        try {
            URL url = new URL(fileurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            int code = conn.getResponseCode();
            if (code == 200) {
                int max = conn.getContentLength();
                pd.setMax(max);
                InputStream is = conn.getInputStream();
                File file = new File(path);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                int total = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    Thread.sleep(5);
                    pd.setProgress(total);
                }
                is.close();
                fos.close();
                return file;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
