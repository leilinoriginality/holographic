package com.zhilianxinke.holographic.module.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhilianxinke.holographic.BaseFragment;
import com.zhilianxinke.holographic.LoginActivity;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.entity.AppUsers;
import com.zhilianxinke.holographic.module.user.widget.CircularImage;
import com.zhilianxinke.holographic.utils.AppUtils;
import com.zhilianxinke.holographic.utils.CacheUtils;
import com.zhilianxinke.holographic.utils.NetworkInfoManager;
import com.zhilianxinke.holographic.utils.SDCardUtils;
import com.zhilianxinke.holographic.utils.UpdateManager;
import com.zhilianxinke.holographic.utils.WinToast;
import com.zhilianxinke.holographic.utils.dialog.SweetAlertDialog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ldb on 2016/1/21.
 */
public class UserFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.cover_user_photo)
    CircularImage coverUserPhoto;
    @Bind(R.id.lin_view)
    ImageView linView;
    @Bind(R.id.loading)
    RelativeLayout loading;
    @Bind(R.id.register)
    RelativeLayout register;
    @Bind(R.id.title)
    RelativeLayout title;
    @Bind(R.id.line)
    View line;
    @Bind(R.id.clear_cache)
    RelativeLayout clearCache;
    @Bind(R.id.update_upgrade)
    RelativeLayout updateUpgrade;
    @Bind(R.id.login_text)
    TextView loginText;
    AppUsers appUsers;
    private final String login_ok = "已登录";
    private final String STATE = "state";
    private final String CLEAR_UPDATE_DATA = "clear_update_data";
    @Bind(R.id.cache_size)
    TextView cacheSize;
    @Bind(R.id.version)
    TextView version;

    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment;
    }

    @Override
    protected void initListener() {
        clearCache.setOnClickListener(this);
        updateUpgrade.setOnClickListener(this);
        loading.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    protected void initialized() {
        version.setText(AppUtils.getVersionName(getActivity()));
        String size = SDCardUtils.getTotalCacheSize(getContext());
        if (!size.equals(".00B")) {
            cacheSize.setText(size);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        appUsers = (AppUsers) CacheUtils.readObject(getActivity());
        if (appUsers != null) {
            loginText.setText(login_ok);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_cache:
                //清除缓存
                clearCache();
                break;
            case R.id.update_upgrade:
                //升级
                if (NetworkInfoManager.isNetworkAvailable(getActivity())) {
                    UpdateManager manager = new UpdateManager();
                    manager.QueryApkVersion(getActivity(), requestQueue, urlBuilder, true);
                } else {
                    WinToast.toast(getActivity(), R.string.check_noteWork);
                }
                break;
            case R.id.loading:
                //登录
                if (appUsers == null) {
                    //TODO 判断权限
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra(STATE, false);
                    startActivity(intent);
                }
                break;
            case R.id.register:
                //注册
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(STATE, true);
                startActivity(intent);
                break;
        }
    }

    public void clearCache() {
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("当前可清理缓存" + SDCardUtils.getTotalCacheSize(getContext()))
                .setContentText("")
                .setCancelText(getActivity().getString(R.string.cancel))
                .setConfirmText("清除")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SDCardUtils.clearAllCache(getContext());
                        Intent intent = new Intent(CLEAR_UPDATE_DATA);
                        getActivity().sendBroadcast(intent);
                        cacheSize.setText("");
                        sDialog.setTitleText("清除成功!")
                                .setContentText("")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                    }
                })
                .show();
    }


}
