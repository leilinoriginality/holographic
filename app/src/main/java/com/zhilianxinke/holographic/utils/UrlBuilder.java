package com.zhilianxinke.holographic.utils;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by hh on 2015-04-26.
 */
public class UrlBuilder {
    public final String serverUrl = "http://115.28.129.68:9988";
    //public final String serverUrl = "http://192.168.1.45:18088";//杨文与
    public final String serverName = "Holographic";
    public final String baseUrl = serverUrl + "/" + serverName;

    public final String getInfo = "/api/getInfo";//首页获取最新信息接口
    public final String findByFuzzyQuery = "/api/findByFuzzyQuery";//搜素
    public final String getMassage = "/api/setMassage";//获取短信验证码
    public final String register = "/api/register";//注册
    public final String connect = "/api/connect";//登录
    public final String lastVersion = "/api/lastVersion";//版本检测
    public final String SweepQRCode = "/api/SweepQRCode";//二维码扫描
    public final String getAdvert = "/api/getAdvert";//广告
    public final String getBySectionId = "/api/getBySectionId";//发现


    /**
     * 构建请求url
     *
     * @param route
     * @param params
     * @return
     */
    public  String build(String route, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(serverUrl).append("/").append(serverName);
        sb.append(route);
        if (params != null && params.size() > 0) {
            int index = 0;
            for (String key : params.keySet()) {
                String mark = index++ == 0 ? "?" : "&";
                try {
                    String value = URLEncoder.encode(params.get(key), "UTF-8");
                    sb.append(mark).append(key).append("=").append(value);
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        return sb.toString();
    }

    public  String buildImageUrl(String url) {
        StringBuilder sb = new StringBuilder(serverUrl).append("/").append(serverName);
        sb.append("/upload/");
        sb.append(url);
        return sb.toString();
    }

    public static String thumbnailImageUrl(String url) {
        StringBuilder STR = new StringBuilder();
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (!(url.charAt(i) + "").equals("\\")) {
                STR.append(url.charAt(i));
            }
        }
        return STR.toString();
    }

    /**
     * 转换头像路径
     *
     * @param path
     * @return
     */
    public  Uri portrait(String path) {
        Uri uri = null;
        if (path != null) {
            uri = Uri.parse(new StringBuilder(serverUrl).append("/").append(serverName).append("/").append("update").append("/").append(path).toString());
        }
        return uri;
    }

}
