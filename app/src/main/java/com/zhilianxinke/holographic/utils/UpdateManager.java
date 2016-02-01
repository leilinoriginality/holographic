package com.zhilianxinke.holographic.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zhilianxinke.holographic.common.FastJsonRequest;
import com.zhilianxinke.holographic.common.SdkHttpResult;
import com.zhilianxinke.holographic.entity.AppVersion;
import com.zhilianxinke.holographic.utils.dialog.SweetAlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuhao
 * @date 2015-03-06
 */

public class UpdateManager {
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;

    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Context mContext;
    private int versionCode;
    private String versionName;

    private AppVersion _appVersion;

    private boolean mIsShowResult;

    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private UrlBuilder urlBuilder;
    private RequestQueue requestQueue;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    s.progressBar.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public void QueryApkVersion(Context context, RequestQueue requestQueue, UrlBuilder urlBuilder, boolean isShowResult) {
        mContext = context;
        mIsShowResult = isShowResult;
        this.urlBuilder = urlBuilder;
        this.requestQueue = requestQueue;
        if (buildVersionInfo()) {
            checkVersion();
        }
    }


    public void checkVersion() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("version", versionCode+"");
        String url = urlBuilder.build(urlBuilder.lastVersion, map);
        L.i("检测版本：" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult sdkHttpResult) {
                if (sdkHttpResult.getCode() == 200) {
                    _appVersion = JSON.parseObject(sdkHttpResult.getResult(), AppVersion.class);
                    L.i("我的版本：" + AppUtils.getAppName(mContext));
                    if (_appVersion.getVersion()>versionCode) {
                        // 显示提示对话框
                        showNoticeDialog(_appVersion.getFeature());
                    } else {
                        if (mIsShowResult) {
                            WinToast.toast(mContext, "已是最新版本");
                        }
                    }
                } else {
                    if (mIsShowResult) {
                        WinToast.toast(mContext, sdkHttpResult.getResult());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(fastJsonRequest);
    }

    /**
     * 获取软件版本信息
     *
     * @return
     */
    private boolean buildVersionInfo() {
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
            return true;
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog(String noticeContent) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("检测到新版本，需要更新吗?")
                .setContentText(noticeContent)
                .setCancelText("稍后再说")
                .setConfirmText("更新")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        showDownloadDialog();
                    }
                })
                .show();
    }

    /**
     * 显示软件下载对话框
     */
    SweetAlertDialog s;

    private void showDownloadDialog() {
        s = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        s.show();
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";

                    URL url = new URL(_appVersion.getUrl());
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "holographic.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }

    ;

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, "holographic.apk");
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}