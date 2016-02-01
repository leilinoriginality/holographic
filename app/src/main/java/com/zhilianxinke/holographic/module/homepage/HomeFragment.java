package com.zhilianxinke.holographic.module.homepage;/**
 * Created by Ldb on 2016/1/25.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zhilianxinke.holographic.BaseFragment;
import com.zhilianxinke.holographic.MainActivity;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.common.FastJsonRequest;
import com.zhilianxinke.holographic.common.SdkHttpResult;
import com.zhilianxinke.holographic.entity.AppUsers;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.module.homepage.adapter.HomepageAdapter;
import com.zhilianxinke.holographic.utils.CacheUtils;
import com.zhilianxinke.holographic.utils.L;
import com.zhilianxinke.holographic.utils.NetworkInfoManager;
import com.zhilianxinke.holographic.utils.SQLutils;
import com.zhilianxinke.holographic.utils.WinToast;
import com.zhilianxinke.holographic.utils.dialog.SweetAlertDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, MainActivity.ResultListener, AdapterView.OnItemLongClickListener {

    @Bind(R.id.text)
    TextView text;
    @Bind(R.id.lin_view)
    View linView;
    @Bind(R.id.search_IV)
    ImageView searchIV;
    @Bind(R.id.ZXing_IV)
    ImageView ZXingIV;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.no_user)
    RelativeLayout noUser;
    @Bind(R.id.capture_scan_line)
    ImageView captureScanLine;
    @Bind(R.id.capture_crop_layout)
    RelativeLayout captureCropLayout;


    private HomepageAdapter adapter;
    private List<AppVideoInfo> videoInfos;
    private final int HANDLER_GET_VIDEO_SUCCESS = 1;
    private final int HANDLER_ZXING_GET_VIDEO_SUCCESS = 2;
    private final int SEARCH_CODE = 1;
    private final int ZXING_CODE = 2;
    private final String CLEAR_UPDATE_DATA = "clear_update_data";
    private AppUsers appUsers;
    private final int isNotFind = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;

    }

    @Override
    protected void initListener() {
        ZXingIV.setOnClickListener(this);
        searchIV.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        captureCropLayout.setOnClickListener(this);
    }

    @Override
    protected void initialized() {
        appUsers = (AppUsers) CacheUtils.readObject(getActivity());
        adapter = new HomepageAdapter(getContext(), videoInfos);
        adapter.setGridView(gridView);
        gridView.setAdapter(adapter);
        handler.sendEmptyMessage(HANDLER_GET_VIDEO_SUCCESS);
        if (NetworkInfoManager.isNetworkAvailable(getContext())) {
            if (appUsers != null) {
                noUser.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                getVideolist(appUsers.getId());
            } else {
                noUser.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
                ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 2.0f);
                animation.setRepeatCount(-1);
                animation.setRepeatMode(Animation.RESTART);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(1200);
                captureScanLine.startAnimation(animation);
            }
        }
        ((MainActivity) getActivity()).setResultListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CLEAR_UPDATE_DATA);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_GET_VIDEO_SUCCESS:
                    videoInfos = SQLutils.getAlllVideo(isNotFind);
                    L.i("获取数据的长度:" + videoInfos.size());
                    adapter.setData(videoInfos);
                    adapter.notifyDataSetChanged();
                    break;
                case HANDLER_ZXING_GET_VIDEO_SUCCESS:
                    mDialog.dismiss();
                    gridView.setVisibility(View.VISIBLE);
                    noUser.setVisibility(View.GONE);
                    videoInfos = SQLutils.getAlllVideo(isNotFind);
                    L.i("获取数据的长度:" + videoInfos.size());
                    adapter.setData(videoInfos);
                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };

    /**
     * 取得服务端的视频列表
     **/
    private void getVideolist(String uid) {
        Map<String, String> param = new HashMap<>();
        param.put("uid", uid);
        String url = urlBuilder.build(urlBuilder.getInfo, param);
        L.i("取得服务端的视频列表:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                videoInfos = JSON.parseArray(response.getResult(), AppVideoInfo.class);
                SQLutils.saveVideo(videoInfos, isNotFind);
                handler.sendEmptyMessage(HANDLER_GET_VIDEO_SUCCESS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WinToast.toast(getContext(), R.string.check_noteWork);
            }
        });
        requestQueue.add(fastJsonRequest);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.search_IV:
                //搜索
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getActivity().startActivityForResult(intent, SEARCH_CODE);
                break;
            case R.id.capture_crop_layout:
            case R.id.ZXing_IV:
                //二维码
                appUsers = (AppUsers) CacheUtils.readObject(getContext());
                if (appUsers == null) {
                    WinToast.toast(getActivity(), "请先登录或者注册");
                } else {
                    Intent intent1 = new Intent(getContext(), CaptureActivity.class);
                    getActivity().startActivityForResult(intent1, ZXING_CODE);
                }
                break;
        }
    }

    /**
     * RecyclerView 的单击事件
     **/
    @Override
    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
        final AppVideoInfo appVideoInfo = videoInfos.get(i);
        List<AppVideoInfo> list = SQLutils.findVideo(appVideoInfo);
        for (AppVideoInfo af : list) {
            appVideoInfo.setIsDownload(af.getIsDownload());
            appVideoInfo.setLoacPath(af.getLoacPath());
        }
        appUsers = (AppUsers) CacheUtils.readObject(getActivity());
        if (appUsers == null) {
            WinToast.toast(getActivity(), "请先登录或者注册");
        } else {
            GridViewItemUtils.palyOrDownLoad(getContext(), adapter, appVideoInfo);
        }
    }


    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_CODE) {
            handler.sendEmptyMessage(HANDLER_GET_VIDEO_SUCCESS);
        } else if (requestCode == ZXING_CODE) {
            String result = data.getExtras().getString("result");
            if (result.equals("")) {
                return;
            }
            zxing_getData(result, appUsers.getId());
        }
    }

    private void zxing_getData(String code, String uid) {
        mDialog.setText("正在加载数据...");
        mDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("uid", uid);
        param.put("code", code);
        String url = urlBuilder.build(urlBuilder.SweepQRCode, param);
        L.i("二维码取得服务端的视频列表:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                if (response.getCode() == 200) {
                    videoInfos = JSON.parseArray(response.getResult(), AppVideoInfo.class);
                    SQLutils.saveVideo(videoInfos, isNotFind);
                    handler.sendEmptyMessage(HANDLER_ZXING_GET_VIDEO_SUCCESS);
                } else {
                    mDialog.dismiss();
                    WinToast.toast(getActivity(), response.getResult());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                WinToast.toast(getContext(), R.string.check_noteWork);
            }
        });
        requestQueue.add(fastJsonRequest);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CLEAR_UPDATE_DATA)) {
                initialized();
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final AppVideoInfo appVideoInfo = videoInfos.get(i);
        List<AppVideoInfo> list = SQLutils.findVideo(appVideoInfo);
        for (AppVideoInfo af : list) {
            appVideoInfo.setIsDownload(af.getIsDownload());
            appVideoInfo.setLoacPath(af.getLoacPath());
        }
        if (appVideoInfo.getLoacPath() != null && !appVideoInfo.getLoacPath().equals("")) {

            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("是否删除" + appVideoInfo.getName())
                    .setContentText("")
                    .setCancelText(getActivity().getString(R.string.cancel))
                    .setConfirmText("删除")
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
                            sDialog.dismiss();
                            adapter.updateItemData(appVideoInfo, false);
                        }
                    })
                    .show();

        }
        return false;
    }
}
