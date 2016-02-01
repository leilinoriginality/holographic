package com.zhilianxinke.holographic.entity;

import java.io.Serializable;

/**
 * Created by Ldb on 2016/1/27.
 */
public class AppUsers implements Serializable{

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
