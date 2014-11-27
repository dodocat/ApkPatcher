package org.quanqi.android.apkpatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.quanqi.android.apkpatcher.engine.PatcherEngine;
import org.quanqi.android.apkpatcher.utils.ApkInfoTool;
import org.quanqi.android.apkpatcher.utils.BeanFactory;
import org.quanqi.android.apkpatcher.utils.DownloadManager;
import org.quanqi.android.apkpatcher.utils.PromptManager;
import org.quanqi.android.apkpatcher.utils.YouHttpTask;

import java.io.File;
import java.io.IOException;

/**
 * 增量升级Demo主逻辑文件(客户端)
 *
 * @author duguang
 * @version 1.0
 * @date 2013.12.29
 * @boke http://blog.csdn.net/duguang77
 */
public class MainActivity extends Activity implements OnClickListener {
    private static final String tag = "MainActivity";

    private String rootPath;

    private Button btn;
    private EditText mTxtOld;
    private EditText mTxtPatcher;
    private EditText mTxtNew;

    private String urlPath;

    static {
        //调用.so文件,引入打包库
        System.loadLibrary("Patcher");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case 0:
                    mTxtOld.setText(data.getData().getEncodedPath());
                    break;

                case 1:
                    mTxtPatcher.setText(data.getData().getEncodedPath());
                    break;

            }
        } catch (Exception ex) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        setContentView(R.layout.activity_main);
        //checkOut();
        initView();
        setListenet();
    }

    /**
     * 调用.so库中的方法,合并apk
     *
     * @param old    旧Apk地址
     * @param newapk 新apk地址(名字)
     * @param patch  增量包地址
     */
    public native void patcher(String old, String newapk, String patch);

    /**
     * 初始化控件
     */
    private void initView() {
        btn = (Button) this.findViewById(R.id.button1);
        mTxtOld = (EditText) this.findViewById(R.id.text_old);

        mTxtNew = (EditText) this.findViewById(R.id.text_new);
        mTxtPatcher = (EditText) this.findViewById(R.id.text_patcher);
        mTxtOld.setText("/sdcard/Download/1.apk");
        mTxtNew.setText("2");
        mTxtPatcher.setText("/sdcard/Download/1-2.p");
        Log.i(tag, "version code: " + String.valueOf(ApkInfoTool.getVersionCode(getApplicationContext())));
        Log.i(tag, "version name: " + ApkInfoTool.getVersionName(getApplicationContext()));
    }

    /**
     * 设置监听
     */
    private void setListenet() {
        btn.setOnClickListener(this);
        mTxtOld.setOnClickListener(this);
        mTxtPatcher.setOnClickListener(this);

    }

    /**
     * 所有的点击事件
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                if (isFileEmpty()) {
                    exAsyncTask();
                }
                break;
            case R.id.text_old:
                selectFile(0);
                break;
            case R.id.text_patcher:
                selectFile(1);
                break;
            default:
                break;
        }
    }

    /**
     * 选择文件
     *
     * @param n 返回的标记
     */
    private void selectFile(int n) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, n);
    }

    /**
     * 判断选择的3个文件如果不为空返回true
     *
     * @return
     */
    private boolean isFileEmpty() {
        if (TextUtils.isEmpty(mTxtOld.getText().toString())) {
            Toast.makeText(MainActivity.this, "请选择旧版本文件", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(mTxtNew.getText().toString())) {
            Toast.makeText(MainActivity.this, "请输入合并后新版本文件名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mTxtPatcher.getText().toString())) {
            Toast.makeText(MainActivity.this, "请选择补丁文件", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 在子线程中完成apk合并,并在主线程安装apk
     */
    private void exAsyncTask() {
        AsyncTask task = new AsyncTask() {

            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this, "正在生成APK...", "请稍等...", true, false);
                progressDialog.show();
            }

            @Override
            protected Object doInBackground(Object... arg0) {
                String newApk = rootPath + File.separator + mTxtNew.getText().toString() + ".apk";
                File file = new File(newApk);
                if (file.exists())
                    file.delete();//如果newApk文件已经存在,先删除

                //调用.so库中的方法,把增量包和老的apk包合并成新的apk
                patcher(mTxtOld.getText().toString(), newApk, mTxtPatcher.getText().toString());
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "打包完成，安装。。。。", Toast.LENGTH_SHORT).show();
                String newApk = rootPath + File.separator + mTxtNew.getText().toString() + ".apk";

                installApk(newApk);

            }

        };
        task.execute();
    }

    /**
     * 安装指定地址(filePath)的apk
     */
    private void installApk(String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
//		String filePath = rootPath + File.separator + mTxtNew.getText().toString() + ".apk";
        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        startActivity(i);
    }

    /**
     * 检测新版本
     */
    public void checkOut() {

        YouHttpTask<Context, Boolean> task = new YouHttpTask<Context, Boolean>() {

            @Override
            protected void onPreExecute() {
                PromptManager.showProgressDialog(MainActivity.this);
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Context... params) {
                PatcherEngine engine = BeanFactory.getInstance(PatcherEngine.class);
                boolean result = engine.isVersionCode(MainActivity.this);
                urlPath = engine.getUpGrade(MainActivity.this);
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                Log.i(tag, "修改之前.............");
                if (result) {
                    showUpdateDialog();
                    PromptManager.closeProgressDialog();
                    super.onPostExecute(result);
                } else {
                    PromptManager.closeProgressDialog();
                    PromptManager.showToast(getApplicationContext(), "版本已经是最新的...");
                }
            }
        };
        task.setContext(this);
        task.execute(MainActivity.this);
    }


    /**
     * 显示更新对话框
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("更新提醒");
        builder.setMessage("1.增加数据库...");
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });

        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(tag, "下载最新版本:" + ",替换安装");
                final ProgressDialog pd = new ProgressDialog(
                        MainActivity.this);
                pd.setTitle("更新提醒:");
                pd.setMessage("正在下载更新apk");
                // 显示指定水平方向的进度条
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    new Thread() {
                        public void run() {
                            File f = new File(Environment
                                    .getExternalStorageDirectory(), "temp.patch");

                            //获取服务器上生成的差异包地址result
//							PatcherEngine engine = BeanFactory.getInstance(PatcherEngine.class);
//							String result = engine.getUpGrade(MainActivity.this);

                            File file = null;
//							if(result!=null){
                            file = DownloadManager.download(
                                    urlPath,
                                    f.getAbsolutePath(), pd);
//								Log.i(tag, result);
//							}

                            if (file == null) {
                                Log.i(tag, "下载的文件不存在");
                            } else {
                                Log.i(tag, "下载成功替换安装");
                                try {
                                    ApkInfoTool.backupApp("com.dodola.patcher", MainActivity.this);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                String patch = rootPath + File.separator + "temp.patch";
                                String newApk = rootPath + File.separator + "new.apk";
                                String oldApk = rootPath + File.separator + "com.dodola.patcher.apk";
                                patcher(oldApk, newApk, patch);
                                installApk(newApk);

                            }
                            pd.dismiss();
                        }

                        ;
                    }.start();
                } else {
                    Toast.makeText(getApplicationContext(), "sd卡不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "下次再说", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


}
