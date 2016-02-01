package com.zhilianxinke.holographic;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

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
public abstract class BaseActivity extends FragmentActivity {

    /**
     * 全局的Context {@link #mContext = this.getApplicationContext();}
     */
    protected Context mContext;
    protected RequestQueue requestQueue;
    protected LoadingDialog mDialog;
    protected UrlBuilder urlBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            this.setContentView(layoutId);
            ButterKnife.bind(this);
            //steepTitle();
            // 删除窗口背景
            //getWindow().setBackgroundDrawable(null);
        }
        urlBuilder = new UrlBuilder();
        mDialog = new LoadingDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        mContext = this.getApplicationContext();
        ((BaseApplication) this.getApplication()).addActivity(this);
        // 向用户展示信息前的准备工作在这个方法里处理
        preliminary();
    }

    /**
     * 加载沉浸式状态栏
     **/
    public void steepTitle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

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

    /**
     * 默认退出
     */
    public void defaultFinish() {
        super.finish();
    }
}
