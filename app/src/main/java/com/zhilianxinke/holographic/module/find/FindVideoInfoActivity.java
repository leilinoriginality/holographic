package com.zhilianxinke.holographic.module.find;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zhilianxinke.holographic.BaseActivity;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.common.FastJsonRequest;
import com.zhilianxinke.holographic.common.SdkHttpResult;
import com.zhilianxinke.holographic.entity.AppUsers;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.entity.VideoFile;
import com.zhilianxinke.holographic.module.homepage.GridViewItemUtils;
import com.zhilianxinke.holographic.module.homepage.SearchActivity;
import com.zhilianxinke.holographic.module.homepage.adapter.HomepageAdapter;
import com.zhilianxinke.holographic.play.VideoViewPlayingActivity;
import com.zhilianxinke.holographic.utils.CacheUtils;
import com.zhilianxinke.holographic.utils.L;
import com.zhilianxinke.holographic.utils.SDCardUtils;
import com.zhilianxinke.holographic.utils.WinToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ldb on 2016/1/29.
 */
public class FindVideoInfoActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    @Bind(R.id.back)
    TextView back;
    @Bind(R.id.type)
    TextView type;

    @Bind(R.id.search_IV)
    ImageView searchIV;

    @Bind(R.id.gridView)
    GridView gridView;

    private final int SEARCH_CODE = 1;
    private final int ZXING_CODE = 2;
    AppUsers appUsers;
    List<AppVideoInfo> appVideoInfoList;
    private final int HANDLER_GET_VIDEO_SUCCESS = 1;
    HomepageAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.findvideoinfo_activity;
    }

    @Override
    protected void initListener() {
        appUsers = (AppUsers) CacheUtils.readObject(this);
        String typeName = getIntent().getStringExtra("typeName");
        String typeId = getIntent().getStringExtra("typeId");
        type.setText(typeName);
        adapter = new HomepageAdapter(this, appVideoInfoList);
        adapter.setGridView(gridView);
        gridView.setAdapter(adapter);
        getVideoBySectionId(typeId, appUsers);
    }

    @Override
    protected void initialized() {
        back.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
//        /ZXingIV.setOnClickListener(this);
        searchIV.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_GET_VIDEO_SUCCESS:
                    adapter.setData(appVideoInfoList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    protected void getVideoBySectionId(String sectionId, AppUsers appUsers) {
        if (sectionId == null) {
            return;
        }
        mDialog.setText("正在加载...");
        mDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("parSectionId", sectionId);
        if (appUsers == null) {
            map.put("uid", "");
        } else {
            map.put("uid", appUsers.getId());
        }
        String url = urlBuilder.build(urlBuilder.getBySectionId, map);
        L.i("获取某一类视屏:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                mDialog.dismiss();
                appVideoInfoList = JSON.parseArray(response.getResult(), AppVideoInfo.class);
                handler.sendEmptyMessage(HANDLER_GET_VIDEO_SUCCESS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WinToast.toast(FindVideoInfoActivity.this, R.string.check_noteWork);
                mDialog.dismiss();
            }
        });

        requestQueue.add(fastJsonRequest);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.search_IV:
                //搜索
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivityForResult(intent, SEARCH_CODE);
                break;
           /* case R.id.ZXing_IV:
                //二维码
                appUsers = (AppUsers) CacheUtils.readObject(getContext());
                if (appUsers == null) {
                    WinToast.toast(this, "请先登录或者注册");
                } else {
                    Intent intent1 = new Intent(getContext(), CaptureActivity.class);
                    startActivityForResult(intent1, ZXING_CODE);
                }
                break;*/
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final AppVideoInfo appVideoInfo = appVideoInfoList.get(i);
        List<VideoFile> videoFiles = SDCardUtils.getAllDownLoadVideo();
        String locaPath = "";
        for (int j = 0; j < videoFiles.size(); j++) {
            if (videoFiles.get(j).getFileName().contains(appVideoInfo.getName())) {
                locaPath = videoFiles.get(j).getFilePath();
            }
        }
        appUsers = (AppUsers) CacheUtils.readObject(this);
        if (appUsers == null) {
            WinToast.toast(this, "请先登录或者注册");
        } else {
            if (locaPath.equals("")) {
                GridViewItemUtils.palyOrDownLoad(this, adapter, appVideoInfo);
            } else {
                Intent intent = new Intent(this, VideoViewPlayingActivity.class);
                intent.setData(Uri.parse(locaPath));
//                intent.putExtra("playPath", locaPath);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    appVideoInfoList = JSON.parseArray(response.getResult(), AppVideoInfo.class);
                    handler.sendEmptyMessage(HANDLER_GET_VIDEO_SUCCESS);
                } else {
                    mDialog.dismiss();
                    WinToast.toast(FindVideoInfoActivity.this, response.getResult());
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
}
