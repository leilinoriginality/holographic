package com.zhilianxinke.holographic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zhilianxinke.holographic.utils.LoadingDialog;
import com.zhilianxinke.holographic.utils.UrlBuilder;

import butterknife.ButterKnife;

/**
 * 功能描述：对Activity类进行扩展
 *
 * @author Ldb
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 全局的Context {@link #mContext = this.getApplicationContext();}
     */
    protected Context mContext;
    protected RequestQueue requestQueue;
    protected LoadingDialog mDialog;
    protected UrlBuilder urlBuilder;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        view=inflater.inflate(layoutId,null);
        ButterKnife.bind(this,view);
        urlBuilder = new UrlBuilder();
        mContext = this.getActivity();
        mDialog = new LoadingDialog(mContext);
        requestQueue = Volley.newRequestQueue(mContext);
        // 向用户展示信息前的准备工作在这个方法里处理
        preliminary();
        return view;
    }

    /**
     * 向用户展示信息前的准备工作在这个方法里处理
     */
    protected void preliminary() {
        // 初始化组件
        initListener();
        // 初始化数据
        initialized();
    }

    /**
     * 获取全局的Context
     *
     * @return {@link #mContext = this.getApplicationContext();}
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 布局文件ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initialized();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
