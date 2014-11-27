package com.dodola.patcher.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.dodola.patcher.GloableParams;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 向服务器发送Http请求的工具类
 *
 */
public class HttpClientUtils {
    private HttpClient client;
    private HttpHost host;

    public HttpClientUtils() {
        client = new DefaultHttpClient();
        client.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
        if (StringUtils.isNotBlank(GloableParams.PROXY_IP)) {
            host = new HttpHost(GloableParams.PROXY_IP,
                    GloableParams.PROXY_PORT);
            client.getParams()
                    .setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
        }
    }

    /**
     * 获取图片
     *
     * @param path
     * @return
     */
    public Bitmap getBitmap(String path) {
        HttpGet get = new HttpGet(path);
        try {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + "/redbaby/imagecache");
            if (!dir.exists())
                dir.mkdirs();
            final File file = new File(
                    Environment.getExternalStorageDirectory()
                            + "/redbaby/imagecache/"
                            + URLEncoder.encode(path, "UTF-8"));
            if (file.exists()) {
                long lastModified = file.lastModified();
                SimpleDateFormat format = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                String time = format.format(lastModified) + " GMT";
                Log.i("time", time);
                get.setHeader("If-Modified-Since", time);
            }
            HttpResponse httpResponse = client.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.i("getimage", "200");
                HttpEntity entity = httpResponse.getEntity();
                InputStream is = entity.getContent();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int length;
                while ((length = is.read(buffer)) != -1)
                    out.write(buffer, 0, length);

                byte[] data = out.toByteArray();
                out.close();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
                return BitmapFactory.decodeByteArray(data, 0, data.length);

            } else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
                Log.i("getimage", "304");
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param jsonStr
     * @return
     */
    public String sendPostJson(String url, String jsonStr) {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("body", jsonStr));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        httpPost.setEntity(entity);
        HttpResponse httpResponse;
        try {
            httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送get请求
     *
     * @param url
     * @return
     */
    public String sendGetJson(String url) {
        HttpGet httpRequest = new HttpGet(url);
        HttpResponse httpResponse;
        try {
            httpResponse = client.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            } else {
                //T
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
