package com.zhilianxinke.holographic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;

import com.zhilianxinke.holographic.common.FragmentTabHost;
import com.zhilianxinke.holographic.module.find.FindFragment;
import com.zhilianxinke.holographic.module.homepage.HomeFragment;
import com.zhilianxinke.holographic.module.user.UserFragment;
import com.zhilianxinke.holographic.utils.NetworkInfoManager;
import com.zhilianxinke.holographic.utils.UpdateManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Bind(R.id.realtabcontent)
    FrameLayout realtabcontent;
    @Bind(R.id.tabcontent)
    FrameLayout tabcontent;

    @Bind(R.id.tabhost)
    FragmentTabHost tabhost;
    private Class fragmentArray[] = {HomeFragment.class, FindFragment.class, UserFragment.class};
    private String mTextviewArray[] = {"首页", "发现", "我的"};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_home_btn, R.drawable.tab_find_btn, R.drawable.tab_my_btn};
    private boolean isCheckQueryApkVersion = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;

    }

    @Override
    protected void initListener() {
        if (NetworkInfoManager.isNetworkAvailable(this)) {
            //当前有可用网络
            if (isCheckQueryApkVersion) {
                UpdateManager updateManager = new UpdateManager();
                updateManager.QueryApkVersion(this, requestQueue, urlBuilder, false);
                isCheckQueryApkVersion = false;
            }
        }
    }

    @Override
    protected void initialized() {
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        for (int i = 0; i < 3; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            tabhost.addTab(tabSpec, fragmentArray[i], null);
        }
        tabhost.getTabWidget().setDividerDrawable(null);
    }

    /*
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = getLayoutInflater().inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private ResultListener resultListener;

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public interface ResultListener {
        public void onResult(int requestCode, int resultCode, Intent data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resultListener.onResult(requestCode, resultCode, data);

    }
}
