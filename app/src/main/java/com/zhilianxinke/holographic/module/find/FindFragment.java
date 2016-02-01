package com.zhilianxinke.holographic.module.find;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zhilianxinke.holographic.BaseFragment;
import com.zhilianxinke.holographic.MainActivity;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.common.FastJsonRequest;
import com.zhilianxinke.holographic.common.SdkHttpResult;
import com.zhilianxinke.holographic.entity.AppCommerical;
import com.zhilianxinke.holographic.entity.AppUsers;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.entity.FindVideo;
import com.zhilianxinke.holographic.module.find.adapter.FindVideoAdapter;
import com.zhilianxinke.holographic.module.find.widget.CBViewHolderCreator;
import com.zhilianxinke.holographic.module.find.widget.ConvenientBanner;
import com.zhilianxinke.holographic.module.find.widget.convenientbannerdemo.NetworkImageHolderView;
import com.zhilianxinke.holographic.module.find.widget.view.XListView;
import com.zhilianxinke.holographic.module.homepage.SearchActivity;
import com.zhilianxinke.holographic.utils.CacheUtils;
import com.zhilianxinke.holographic.utils.L;
import com.zhilianxinke.holographic.utils.NetworkInfoManager;
import com.zhilianxinke.holographic.utils.SQLutils;
import com.zhilianxinke.holographic.utils.WinToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Ldb on 2016/1/21.
 */
public class FindFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, XListView.IXListViewListener ,MainActivity.ResultListener {


    @Bind(R.id.text)
    TextView text;
    @Bind(R.id.search_IV)
    ImageView searchIV;
    @Bind(R.id.listView)
    XListView listView;
    private final int SEARCH_CODE = 1;
    private final int ZXING_CODE = 2;
    private AppUsers appUsers;
    private Handler mHandler;
    private boolean isLoadMore = true;
    private ConvenientBanner convenientBanner;// 顶部广告栏控件
    private List<String> networkImages;
    private String[] images;
    private ArrayList<String> transformerList = new ArrayList<String>();
    private List<AppCommerical> appCommericalList;
    private final int HANDLER_COMMERICA_SUCCSESS = 1;//广告
    private final int HANDLER_FIND_VIDEO_SUCCSESS = 2;//发现视频
    private final int HANDLER_SEARCH_VIDEO_SUCCSESS = 3;//扫描二维码

    private final int isFind = 2;
    private List<AppVideoInfo> appVideoInfoList;
    private List<FindVideo> fvList;
    private FindVideoAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    protected void initListener() {
        searchIV.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(true);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);
        listView.setRefreshTime(getTime());
    }

    @Override
    protected void initialized() {
        appUsers = (AppUsers) CacheUtils.readObject(getContext());
        mHandler = new Handler();
        View view = getActivity().getLayoutInflater().inflate(R.layout.convenientbanner, null);
        convenientBanner = (ConvenientBanner) view
                .findViewById(R.id.convenientBanner);
        adapter = new FindVideoAdapter(getActivity(), fvList);
        listView.addHeaderView(view);
        listView.setAdapter(adapter);
        ((MainActivity) getActivity()).setResultListener(this);
        handler.sendEmptyMessage(HANDLER_COMMERICA_SUCCSESS);
        handler.sendEmptyMessage(HANDLER_FIND_VIDEO_SUCCSESS);
        if (NetworkInfoManager.isNetworkAvailable(getActivity())) {
            getAdvertisement();
            getFindVideo();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_IV:
                //搜索
                //搜索
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getActivity().startActivityForResult(intent, SEARCH_CODE);
                break;
          /*  case R.id.ZXing_IV:
                //二维码
                appUsers = (AppUsers) CacheUtils.readObject(getContext());
                if (appUsers == null) {
                    WinToast.toast(getActivity(), "请先登录或者注册");
                } else {
                    Intent intent1 = new Intent(getActivity(), CaptureActivity.class);
                    getActivity().startActivityForResult(intent1, ZXING_CODE);
                }
                break;*/
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    private void init(String[] images) {
        if (images == null) {
            return;
        }
        //网络加载例子
        networkImages = Arrays.asList(images);
        convenientBanner.setPages(new
                                          CBViewHolderCreator<NetworkImageHolderView>() {
                                              @Override
                                              public NetworkImageHolderView createHolder() {
                                                  return new NetworkImageHolderView();
                                              }
                                          }, networkImages)
                .setPageIndicator(
                        new int[]{R.drawable.ic_page_indicator,
                                R.drawable.ic_page_indicator_focused})
                        // 设置翻页的效果，不需要翻页效果可用不设
                .setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer);
        // 本地图片例子
       /* convenientBanner
                .setPages(new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)*/
        // 设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
        // convenientBanner.setManualPageable(false);设置不能手动影响
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_COMMERICA_SUCCSESS:
                    //获取广告成功
                    appCommericalList = SQLutils.getAppCommerical();
                    if (appCommericalList.size() > 0) {
                        images = new String[appCommericalList.size()];
                        for (int i = 0; i < appCommericalList.size(); i++) {
                            images[i] = appCommericalList.get(i).getUrl();
                        }
                    }
                    init(images);
                    break;

                case HANDLER_FIND_VIDEO_SUCCSESS:
                    //获取视频成功
                    appVideoInfoList = SQLutils.getAlllVideo(isFind);
                    fvList = dateConvert(appVideoInfoList);
                    adapter.setList(fvList);
                    adapter.notifyDataSetChanged();
                    break;
                case HANDLER_SEARCH_VIDEO_SUCCSESS:
                    //二维码
                    fvList = dateConvert(appVideoInfoList);
                    adapter.setList(fvList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        // 开始自动翻页
        convenientBanner.startTurning(5000);
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        // 停止翻页
        convenientBanner.stopTurning();
    }

    // 点击切换效果
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        String name = transformerList.get(position);
        ConvenientBanner.Transformer transformer = ConvenientBanner.Transformer.valueOf(name);
        convenientBanner.setPageTransformer(transformer);
    }


    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 2500);

    }

    private void onLoad() {
        listView.stopRefresh();
        listView.stopLoadMore();
        listView.setRefreshTime(getTime());
    }

    @Override
    public void onLoadMore() {
    /*	if (isLoadMore) {
            isLoadMore = false;*/
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 2500);
        // }

    }

    /**
     * 获取的广告
     **/
    private void getAdvertisement() {
        String url = urlBuilder.baseUrl + urlBuilder.getAdvert;
        L.i("获取广告:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                appCommericalList = JSON.parseArray(response.getResult(), AppCommerical.class);
                SQLutils.saveAppCommerical(appCommericalList);
                handler.sendEmptyMessage(HANDLER_COMMERICA_SUCCSESS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WinToast.toast(getContext(), R.string.check_noteWork);
            }
        });
        requestQueue.add(fastJsonRequest);
    }

    /**
     * 获取的发现数据
     **/
    private void getFindVideo() {
        Map<String, String> map = new HashMap<>();
        if (appUsers != null) {
            map.put("uid", appUsers.getId());
        } else {
            map.put("uid", "");
        }

        String url = urlBuilder.build(urlBuilder.getBySectionId, map);
        L.i("获取发现 视频:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                appVideoInfoList = JSON.parseArray(response.getResult(), AppVideoInfo.class);
                SQLutils.saveVideo(appVideoInfoList, isFind);
                handler.sendEmptyMessage(HANDLER_FIND_VIDEO_SUCCSESS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WinToast.toast(getContext(), R.string.check_noteWork);
            }
        });
        requestQueue.add(fastJsonRequest);
    }




    private List<FindVideo> dateConvert(List<AppVideoInfo> list) {
        List<FindVideo> fvList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            //取得所有的类别ID
            String[] types = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                types[i] = list.get(i).getSectionId();
            }
            types = timeNotPepeat(types);
            for (int i = 0; i < types.length; i++) {
                FindVideo fv = new FindVideo();
                fv.setTypeId(types[i]);
                List<AppVideoInfo> videos = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    if (types[i].equals(list.get(j).getSectionId())) {
                        fv.setTypeName(list.get(j).getSectionName());
                        videos.add(list.get(j));
                    }
                }
                fv.setAppVideoInfoList(videos);
                fvList.add(fv);
            }

        }
        return fvList;
    }

    /**
     * 去除重复 id
     **/
    public String[] timeNotPepeat(String idS[]) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < idS.length; i++) {
            if (!list.contains(idS[i])) {
                list.add(idS[i]);
            }
        }
        String[] newStr = list.toArray(new String[1]);
        return newStr;
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_CODE) {
            handler.sendEmptyMessage(HANDLER_FIND_VIDEO_SUCCSESS);
        } /*else if (requestCode == ZXING_CODE) {
            String result = data.getExtras().getString("result");
            if (result.equals("")) {
                return;
            }
            zxing_getData(result, appUsers.getId());
        }*/
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
                    mDialog.dismiss();
                    handler.sendEmptyMessage(HANDLER_SEARCH_VIDEO_SUCCSESS);
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

}
