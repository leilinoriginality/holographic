package com.zhilianxinke.holographic.module.homepage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.common.circleprogressbarlib.CircleProgressBar;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.utils.WinToast;

import java.util.List;

/**
 * Created by Ldb on 2016/1/26.
 */
public class HomeRVadapter extends RecyclerView.Adapter<HomeRVadapter.MyViewHolder> implements View.OnClickListener {
    private List<AppVideoInfo> data;
    private Context context;
    private LayoutInflater inflater;
    private List<Integer> mHieght;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private RecyclerView rc;

    public HomeRVadapter(Context context, List<AppVideoInfo> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    public void setRc(RecyclerView rc) {
        this.rc = rc;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (AppVideoInfo) v.getTag());
        }
    }


    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, AppVideoInfo data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setData(List<AppVideoInfo> data) {
        this.data = data;
    }

    private CircleProgressBar circleProgressBar;
    private ImageView videoDownloadState;
    private int position;

    public void prepareDownLoad(View view, final int position) {
        this.position = position;
        videoDownloadState = (ImageView) view.findViewById(R.id.video_download_state);
        circleProgressBar = (CircleProgressBar) view.findViewById(R.id.circleProgressBar);
        videoDownloadState.setVisibility(View.GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        /*this.notifyItemChanged(position);
        this.notifyDataSetChanged();*/
        circleProgressBar.setLoadingCallBack(new CircleProgressBar.LoadingCallBack() {
            @Override
            public void loadingComplete(View v) {
                WinToast.toast(context, "下载完成");
                circleProgressBar.setVisibility(View.GONE);
                videoDownloadState.setImageDrawable(context.getResources().getDrawable(R.drawable.download_ok));
                videoDownloadState.setVisibility(View.VISIBLE);
                //HomeRVadapter.this.notifyItemChanged(position);
               // HomeRVadapter.this.notifyDataSetChanged();
            }
        });
        download();
    }

    void delay() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    int current = 0;
    public void download() {
        current = 0;
        circleProgressBar.requestLayout();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (current <= 100) {
                    current++;
                    circleProgressBar.setProgress(current);
                    delay();
                }
            }
        }.start();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = this.inflater.inflate(R.layout.home_rv_item, null);
        MyViewHolder holder = new MyViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        //绑定数据
        AppVideoInfo videoInfo = data.get(i);
        myViewHolder.videoName.setText(videoInfo.getName());
        myViewHolder.videoSuggest.setText(videoInfo.getDescribes());
        if (videoInfo.getFeeType() == 0) {
            //免费
            myViewHolder.videoPayState.setImageResource(R.drawable.pay_free);
        } else if (videoInfo.getFeeType() == 1) {
            //未购买
            myViewHolder.videoPayState.setImageResource(R.drawable.pay_no);
        } else if (videoInfo.getFeeType() == 2) {
            //已购买
            myViewHolder.videoPayState.setImageResource(R.drawable.pay_ok);
        }
        myViewHolder.videoDownloadState.setImageResource(R.drawable.download_no);
        new BitmapUtils(context).display(myViewHolder.videoCover, videoInfo.getPortrait());
      // videoInfo.setPosition(i);
        myViewHolder.itemView.setTag(videoInfo);
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView videoName, videoSuggest;
        private ImageView videoPayState, videoCover, videoDownloadState;
        private CircleProgressBar circleProgressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            videoName = (TextView) itemView.findViewById(R.id.video_Name);
            videoSuggest = (TextView) itemView.findViewById(R.id.video_suggest);
            videoPayState = (ImageView) itemView.findViewById(R.id.video_pay_state);
            videoCover = (ImageView) itemView.findViewById(R.id.video_cover);
            videoDownloadState = (ImageView) itemView.findViewById(R.id.video_download_state);
            circleProgressBar = (CircleProgressBar) itemView.findViewById(R.id.circleProgressBar);
        }
    }
}
