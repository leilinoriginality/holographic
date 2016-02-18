package com.zhilianxinke.holographic.module.homepage.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.common.circleprogressbarlib.CircleProgressBar;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.utils.L;
import com.zhilianxinke.holographic.utils.SDCardUtils;
import com.zhilianxinke.holographic.utils.SQLutils;
import com.zhilianxinke.holographic.utils.WinToast;

import java.io.File;
import java.util.List;

/**
 * Created by Ldb on 2016/1/27.
 */
public class HomepageAdapter extends BaseAdapter {
    private List<AppVideoInfo> data;
    private Context context;
    private LayoutInflater inflater;
    private GridView gridView;

    public HomepageAdapter(Context context, List<AppVideoInfo> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<AppVideoInfo> data) {
        this.data = data;
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = this.inflater.inflate(R.layout.home_rv_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //绑定数据
        AppVideoInfo videoInfo = data.get(i);
        holder.videoName.setText(videoInfo.getName());
        holder.videosuggest.setText(videoInfo.getDescribes());
        if (videoInfo.getFeeType() == 0) {
            //免费
            holder.videopaystate.setImageResource(R.drawable.pay_free);
            holder.videopaystate.setVisibility(View.GONE);
        } else if (videoInfo.getFeeType() == 1) {
            //未购买
            holder.videopaystate.setImageResource(R.drawable.pay_no);
            holder.videopaystate.setVisibility(View.GONE);
        } else if (videoInfo.getFeeType() == 2) {
            //已购买
            holder.videopaystate.setVisibility(View.VISIBLE);
            holder.videopaystate.setImageResource(R.drawable.pay_ok);
        }

        boolean isDownLoad = videoInfo.getIsDownload();
        if (!videoInfo.getIsDownload()) {
            //校对本地状态
            AppVideoInfo appVideoInfo = new Select().from(AppVideoInfo.class).where("uuid = ?", videoInfo.getUuid()).executeSingle();
            if (appVideoInfo != null) {
                isDownLoad = appVideoInfo.getIsDownload();
            }
        }
//            holder.videodownloadstate.setImageResource(R.drawable.download_ok);
//        } else {
//
//
//            holder.videodownloadstate.setImageResource(R.drawable.download_no);
//        }
        int downloadResourceId = isDownLoad?R.drawable.download_ok:R.drawable.download_no;
        holder.videodownloadstate.setImageResource(downloadResourceId);
        new BitmapUtils(context).display(holder.videocover, videoInfo.getPortrait());
        return view;
    }

    private boolean isDownload;

    public void updateItemData(AppVideoInfo item, boolean isDownload) {
        Message msg = Message.obtain();
        int ids = -1;
        // 进行数据对比获取对应数据在list中的位置
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUuid() == item.getUuid()) {
                ids = i;
            }
        }
        this.isDownload = isDownload;
        msg.arg1 = ids;
        data.set(ids, item);
        han.sendMessage(msg);
    }

    @SuppressLint("HandlerLeak")
    private Handler han = new Handler() {
        public void handleMessage(android.os.Message msg) {
            updateItem(msg.arg1);
        }

        ;
    };


    /**
     * 刷新指定item
     *
     * @param index item在listview中的位置
     */
    private void updateItem(int index) {
        if (gridView == null) {
            return;
        }
        // 获取当前可以看到的item位置
        int visiblePosition = gridView.getFirstVisiblePosition();
        // 如添加headerview后 firstview就是hearderview
        // 所有索引+1 取第一个view
        // View view = listview.getChildAt(index - visiblePosition + 1);
        // 获取点击的view
        View view = gridView.getChildAt(index - visiblePosition);
        final ImageView videoDownloadState = (ImageView) view.findViewById(R.id.video_download_state);
        final CircleProgressBar circleProgressBar = (CircleProgressBar) view.findViewById(R.id.circleProgressBar);
        // 获取mDataList.set(ids, item);更新的数据
        final AppVideoInfo data = (AppVideoInfo) getItem(index);
        if (isDownload) {
            // 重新设置界面显示数据
            videoDownloadState.setVisibility(View.GONE);
            circleProgressBar.setVisibility(View.VISIBLE);
            circleProgressBar.setLoadingCallBack(new CircleProgressBar.LoadingCallBack() {
                @Override
                public void loadingComplete(View v) {
                    Toast.makeText(context, data.getName() + "下载完成", Toast.LENGTH_SHORT).show();
                    // WinToast.toast(context, "下载完成");
                    circleProgressBar.setVisibility(View.GONE);
                    videoDownloadState.setImageDrawable(context.getResources().getDrawable(R.drawable.download_ok));
                    videoDownloadState.setVisibility(View.VISIBLE);
                    SQLutils.updateVideo(data);
                }
            });
            download(videoDownloadState, circleProgressBar, data.getName(), data.getVideoUrl());
        } else {
            SDCardUtils. clearfile(data.getLoacPath());
            data.setLoacPath("");
            data.setIsDownload(false);
            SQLutils.cancleDownlaod(data);
            videoDownloadState.setImageResource(R.drawable.download_no);
            circleProgressBar.setVisibility(View.GONE);
            videoDownloadState.setVisibility(View.VISIBLE);
        }
    }

    public void download(final ImageView videoDownloadState, final CircleProgressBar circleProgressBar, final String fileName, String downloadPath) {
        String path = SDCardUtils.getRootDirectoryPath() + "/" + SDCardUtils.videoPath;
        L.i("下载地址：" + downloadPath);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        path = path + "/" + fileName + ".MP4";
        HttpUtils http = new HttpUtils();
        final String finalPath = path;
        HttpHandler handler = http.download(downloadPath,
                path,
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        circleProgressBar.setProgress((int) ((current * 100) / total));
                        circleProgressBar.invalidateUi();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        WinToast.toast(context, "您下载的资源有误，请联系QQ 306663283");
                        videoDownloadState.setImageResource(R.drawable.download_no);
                        videoDownloadState.setVisibility(View.VISIBLE);
                        circleProgressBar.setVisibility(View.GONE);
                        circleProgressBar.setProgress(0);
                        circleProgressBar.invalidateUi();
                    }
                });


    }


    public class ViewHolder {
        public final ImageView videopaystate;
        public final TextView videoName;
        public final TextView videosuggest;
        public final ImageView videocover;
        public final ImageView videodownloadstate;
        public final CircleProgressBar circleProgressBar;
        public final View root;

        public ViewHolder(View root) {
            videopaystate = (ImageView) root.findViewById(R.id.video_pay_state);
            videoName = (TextView) root.findViewById(R.id.video_Name);
            videosuggest = (TextView) root.findViewById(R.id.video_suggest);
            videocover = (ImageView) root.findViewById(R.id.video_cover);
            videodownloadstate = (ImageView) root.findViewById(R.id.video_download_state);
            circleProgressBar = (CircleProgressBar) root.findViewById(R.id.circleProgressBar);
            this.root = root;
        }
    }
}
