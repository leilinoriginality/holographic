package com.zhilianxinke.holographic.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Ldb on 2016/1/25.
 */

@Table(name = "SearchHistory")
public class SearchHistory extends Model implements Serializable {

    @Column(name = "searchName")
    private String searchName;
    @Column(name = "searchTime")
    private String searchTime;
    @Column(name = "clear")
    private int clear;//清除 0.清除 1 清除

    public SearchHistory() {
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    public int getClear() {
        return clear;
    }

    public void setClear(int clear) {
        this.clear = clear;
    }
}
