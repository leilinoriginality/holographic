package com.zhilianxinke.holographic.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Ldb on 2016/1/26.
 */
@Table(name="AppVideoInfo")
public class AppVideoInfo extends Model implements Serializable {

    @Column(name="uuid")
    private String uuid;
    @Column(name="name")
    private String name;
    @Column(name="describes")
    private String describes;// 视频描述
    @Column(name="sectionName")
    private String sectionName;
    @Column(name="sectionId")
    private String sectionId;
    @Column(name="portrait")
    private String portrait;
    @Column(name="videoUrl")
    private String videoUrl;
    @Column(name="spaceSize")
    private float spaceSize;// 视频大小 单位mb
    @Column(name="timeLength")
    private int timeLength;// 时长 单位秒
    @Column(name="resolution")
    private String resolution;// 分辨率
    @Column(name="source")
    private String source;
    @Column(name="insertTime")
    private String insertTime;
    @Column(name="feeType")
    private int feeType;// 0表示免费 1表示付费未购买 2表示付费已购买
    @Column(name="isDownload")
    private Boolean isDownload=false;//是否下载 true下载  false 没有下载
    @Column(name="loacPath")
    private String loacPath;//本地路径
    @Column(name="isFind")
    private int isFind;//区别 发现 和首页。 首页 1， 发现2

    public int getIsFind() {
        return isFind;
    }

    public void setIsFind(int isFind) {
        this.isFind = isFind;
    }

    public String getLoacPath() {
        return loacPath;
    }

    public void setLoacPath(String loacPath) {
        this.loacPath = loacPath;
    }

    public Boolean getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(Boolean position) {
        this.isDownload = position;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public float getSpaceSize() {
        return spaceSize;
    }

    public void setSpaceSize(float spaceSize) {
        this.spaceSize = spaceSize;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public int getFeeType() {
        return feeType;
    }

    public void setFeeType(int feeType) {
        this.feeType = feeType;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
