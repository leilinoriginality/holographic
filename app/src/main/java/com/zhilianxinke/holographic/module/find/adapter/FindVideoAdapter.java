package com.zhilianxinke.holographic.module.find.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.entity.AppUsers;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.entity.FindVideo;
import com.zhilianxinke.holographic.module.find.FindVideoInfoActivity;
import com.zhilianxinke.holographic.module.find.widget.view.VideoGridView;
import com.zhilianxinke.holographic.module.homepage.GridViewItemUtils;
import com.zhilianxinke.holographic.module.homepage.adapter.HomepageAdapter;
import com.zhilianxinke.holographic.utils.CacheUtils;
import com.zhilianxinke.holographic.utils.SQLutils;
import com.zhilianxinke.holographic.utils.WinToast;
import com.zhilianxinke.holographic.utils.dialog.SweetAlertDialog;

import java.util.List;

/**
 * Created by Ldb on 2016/1/29.
 */
public class FindVideoAdapter extends BaseAdapter {
    private List<FindVideo> list;
    private Context context;
    private LayoutInflater inflater;


    public FindVideoAdapter(Context context, List<FindVideo> fv) {
        this.context = context;
        this.list = fv;
        this.inflater = LayoutInflater.from(context);
    }

    public void setList(List<FindVideo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.vedio_listview_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final FindVideo fv = (FindVideo) getItem(i);
        viewHolder.videotype.setText(fv.getTypeName());
        viewHolder.videotype.setTag(fv.getTypeId());
        viewHolder.videotype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FindVideoInfoActivity.class);
                intent.putExtra("typeName", fv.getTypeName());
                intent.putExtra("typeId", fv.getTypeId());
                context.startActivity(intent);

            }
        });

        final List<AppVideoInfo> appVideoInfoList = fv.getAppVideoInfoList();
        final HomepageAdapter adapte = new HomepageAdapter(context, appVideoInfoList);
        adapte.setGridView(viewHolder.gridView);
        viewHolder.gridView.setAdapter(adapte);
        viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final AppVideoInfo appVideoInfo = appVideoInfoList.get(i);
                List<AppVideoInfo> list = SQLutils.findVideo(appVideoInfo);
                for (AppVideoInfo af : list) {
                    appVideoInfo.setIsDownload(af.getIsDownload());
                    appVideoInfo.setLoacPath(af.getLoacPath());
                }
                AppUsers appUsers = (AppUsers) CacheUtils.readObject(context);
                if (appUsers == null) {
                    WinToast.toast(context, "请先登录或者注册");
                } else {
                    // if (appVideoInfo.getLoacPath()!=null&&!appVideoInfo.getLoacPath().equals("")){
                    GridViewItemUtils.palyOrDownLoad(context, adapte, appVideoInfo);
                    // }
                }
            }
        });

        viewHolder.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final AppVideoInfo appVideoInfo = appVideoInfoList.get(i);
                List<AppVideoInfo> list = SQLutils.findVideo(appVideoInfo);
                for (AppVideoInfo af : list) {
                    appVideoInfo.setIsDownload(af.getIsDownload());
                    appVideoInfo.setLoacPath(af.getLoacPath());
                }
                if (appVideoInfo.getLoacPath() != null && !appVideoInfo.getLoacPath().equals("")) {

                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("是否删除" + appVideoInfo.getName())
                            .setContentText("")
                            .setCancelText(context.getString(R.string.cancel))
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
                                    adapte.updateItemData(appVideoInfo, false);
                                }
                            })
                            .show();

                }


                return false;
            }
        });
        return view;
    }


    public class ViewHolder {
        public final TextView videotype;
        public final VideoGridView gridView;
        public final View root;

        public ViewHolder(View root) {
            videotype = (TextView) root.findViewById(R.id.video_type);
            gridView = (VideoGridView) root.findViewById(R.id.gridView);
            this.root = root;
        }
    }
}
