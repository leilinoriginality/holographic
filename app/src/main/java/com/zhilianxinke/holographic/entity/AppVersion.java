package com.zhilianxinke.holographic.entity;

import java.io.Serializable;

/**
 * Created by Ldb on 2016/1/28.
 */
public class AppVersion implements Serializable {

    private String uuid;
    private int version;// 版本号
    private String feature;// 升级提示信息
    private String url;// 升级url


    public AppVersion() {
    }

    public String getUuid() {

        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
