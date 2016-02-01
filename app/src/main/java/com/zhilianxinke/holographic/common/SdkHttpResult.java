package com.zhilianxinke.holographic.common;

import java.io.Serializable;

/**
 * Created by hh on 2015-04-19.
 */
public class SdkHttpResult implements Serializable {
    private int code;
    private String result;
    private String detail;

    public SdkHttpResult(int code, String result, String detail) {
        this.code = code;
        this.result = result;
        this.detail = detail;
    }

    public SdkHttpResult() {
    }


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}