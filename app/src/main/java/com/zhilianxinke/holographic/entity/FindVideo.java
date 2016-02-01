package com.zhilianxinke.holographic.entity;

import java.util.List;

/**
 * Created by Ldb on 2016/1/29.
 */
public class FindVideo {

    private String typeName;
    private String typeId;
    private List<AppVideoInfo> appVideoInfoList;

    public FindVideo() {
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<AppVideoInfo> getAppVideoInfoList() {
        return appVideoInfoList;
    }

    public void setAppVideoInfoList(List<AppVideoInfo> appVideoInfoList) {
        this.appVideoInfoList = appVideoInfoList;
    }
}
