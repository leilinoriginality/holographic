package com.zhilianxinke.holographic.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Ldb on 2016/1/29.
 */
@Table(name="AppCommerical")
public class AppCommerical extends Model implements Serializable {

    @Column(name="uuid")
    private String uuid;
    @Column(name="url")
    private String url;// 图片路径
    @Column(name="title")
    private String title;// 标题
    @Column(name="content")
    private String content;// 内容提示

    public AppCommerical() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
