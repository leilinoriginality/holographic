package com.zhilianxinke.holographic;

import android.content.Intent;
import android.os.Handler;

/**
 * Created by Ldb on 2016/1/29.
 */
public class WelcomeActivity extends BaseActivity {
    private Handler handler;

    @Override
    protected int getLayoutId() {
        return R.layout.welcome_activity;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initialized() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
