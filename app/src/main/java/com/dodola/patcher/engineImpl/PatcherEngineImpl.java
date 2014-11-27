package com.dodola.patcher.engineImpl;

import android.content.Context;
import android.util.Log;

import com.dodola.patcher.ContantValue;
import com.dodola.patcher.engine.PatcherEngine;
import com.dodola.patcher.utils.ApkInfoTool;
import com.dodola.patcher.utils.HttpClientUtils;


/**
 * 连接服务器的实现类
 *
 */
public class PatcherEngineImpl implements PatcherEngine {

    private static final String tag = "PatherEngineImpl";

    @Override
    public boolean isVersionCode(Context context) {
        // TODO Auto-generated method stub
        HttpClientUtils httpUtils = new HttpClientUtils();
        String jsonStr = String.valueOf(ApkInfoTool.getVersionCode(context));
        System.out.println(jsonStr);
        String postJson = httpUtils.sendPostJson(ContantValue.CHECK_VERSION_CODE_URL, jsonStr);
        if (postJson != null) {
            Log.i(tag, postJson);

            if ("1".equals(postJson)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getUpGrade(Context context) {
        // TODO Auto-generated method stub
        HttpClientUtils httpUtils = new HttpClientUtils();
        String jsonStr = String.valueOf(ApkInfoTool.getVersionCode(context));
        System.out.println(jsonStr);
        String postJson = httpUtils.sendPostJson(ContantValue.UPGRADE_URL, jsonStr);
        if (postJson != null) {
            Log.i(tag, postJson);
            return postJson;
        }
        return null;
    }

}
