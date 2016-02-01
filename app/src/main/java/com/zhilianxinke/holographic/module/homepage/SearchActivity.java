package com.zhilianxinke.holographic.module.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zhilianxinke.holographic.BaseActivity;
import com.zhilianxinke.holographic.MainActivity;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.common.FastJsonRequest;
import com.zhilianxinke.holographic.common.SdkHttpResult;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.entity.SearchHistory;
import com.zhilianxinke.holographic.module.homepage.adapter.HomepageAdapter;
import com.zhilianxinke.holographic.module.homepage.adapter.SearchAdapter;
import com.zhilianxinke.holographic.utils.DateUtils;
import com.zhilianxinke.holographic.utils.KeyBoardUtils;
import com.zhilianxinke.holographic.utils.L;
import com.zhilianxinke.holographic.utils.SQLutils;
import com.zhilianxinke.holographic.utils.WinToast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ldb on 2016/1/25.
 */
public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener, View.OnClickListener, AdapterView.OnItemClickListener {
    @Bind(R.id.cancel)
    TextView cancel;
    @Bind(R.id.input_edit)
    EditText inputEdit;
    @Bind(R.id.syncOperatorImg)
    ImageView syncOperatorImg;
    @Bind(R.id.search_Rl)
    RelativeLayout searchRl;
    @Bind(R.id.auto_list)
    ListView autoList;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.noData)
    TextView noData;

    private SearchAdapter adapter;
    private List<SearchHistory> list;
    private LinkedList<SearchHistory> searchContent;
    private SearchHistory sh;
    private List<AppVideoInfo> videoInfos;
    private final int HANDLER_SEARCH_VIDEO_SUCCESS = 1;
    private HomepageAdapter homeRVadapter;

    @Override
    protected int getLayoutId() {
        return R.layout.search_activity;
    }

    @Override
    protected void initListener() {
        inputEdit.setOnEditorActionListener(this);
        autoList.setOnItemClickListener(this);
        inputEdit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void initialized() {
        list = SQLutils.getAllSearchHistory();
        conversionList(list);
        if (searchContent.size() > 0) {
            sh = new SearchHistory();
            sh.setSearchName(getString(R.string.clear_history));
            sh.setClear(0);
            searchContent.addLast(sh);
        }
        adapter = new SearchAdapter(this, searchContent);
        autoList.setAdapter(adapter);
        KeyBoardUtils.openKeybord(inputEdit, this);

        homeRVadapter = new HomepageAdapter(this, videoInfos);
        homeRVadapter.setGridView(gridView);
        gridView.setAdapter(homeRVadapter);
    }


    /**
     * list转LinkedList
     **/
    public void conversionList(List<SearchHistory> list) {
        searchContent = new LinkedList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                searchContent.add(list.get(i));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (!TextUtils.isEmpty(inputEdit.getText().toString())) {
                sh = new SearchHistory();
                sh.setSearchName(inputEdit.getText().toString());
                sh.setClear(1);
                sh.setSearchTime(DateUtils.currentTime());
                SQLutils.saveSearchHostory(sh);
                inputEdit.clearFocus();
                KeyBoardUtils.closeKeybord(inputEdit, this);
                getVideolist(inputEdit.getText().toString());
            }


        }
        return true;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SEARCH_VIDEO_SUCCESS:
                    autoList.setVisibility(View.GONE);
                    mDialog.dismiss();
                    if (videoInfos.size() <= 0) {
                        gridView.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        gridView.setVisibility(View.VISIBLE);
                        noData.setVisibility(View.GONE);
                        L.i("获取数据的长度:" + videoInfos.size());
                        homeRVadapter.setData(videoInfos);
                        homeRVadapter.notifyDataSetChanged();
                    }


                    break;
            }
        }
    };


    /**
     * 取得服务端的视频列表
     **/
    private void getVideolist(String searchContent) {
        mDialog.show();
        mDialog.setText("正在搜索...");
        Map<String, String> param = new HashMap<>();
        param.put("uid", "");
        // param.put("dt", DateUtils.nowTime());
        param.put("someThing", searchContent);
        String url = urlBuilder.build(urlBuilder.findByFuzzyQuery, param);
        L.i("正在搜索:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                videoInfos = JSON.parseArray(response.getResult(), AppVideoInfo.class);
                videoInfos=SQLutils.findVideo(videoInfos);
                handler.sendEmptyMessage(HANDLER_SEARCH_VIDEO_SUCCESS);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.auto_list) {
            sh = searchContent.get(i);
            if (sh.getClear() != 0) {
                inputEdit.setText(sh.getSearchName());
            } else {
                SQLutils.deleteAllSearchHistory();
                searchContent.clear();
                adapter.setmTitleArray(searchContent);
                adapter.notifyDataSetChanged();
            }
        } else if (adapterView.getId() == R.id.gridView) {
            final AppVideoInfo appVideoInfo = videoInfos.get(i);
            List<AppVideoInfo> list = SQLutils.findVideo(appVideoInfo);
            for (AppVideoInfo af : list) {
                appVideoInfo.setIsDownload(af.getIsDownload());
                appVideoInfo.setLoacPath(af.getLoacPath());
            }
            GridViewItemUtils.palyOrDownLoad(this, homeRVadapter, appVideoInfo);
        }
    }

    /**
     * RecyclerView的单击事件
     **/


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.input_edit:
                autoList.setVisibility(View.VISIBLE);
                break;
            case R.id.cancel:
                KeyBoardUtils.closeKeybord(inputEdit, this);
                if (autoList.getVisibility() == View.VISIBLE) {
                    autoList.setVisibility(View.GONE);
                } else {
                    inputEdit.setText("");
                    Intent intent=new Intent(this, MainActivity.class);
                    this.setResult(1,intent);
                    this.finish();
                }

                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inputEdit.setText("");
    }
}
