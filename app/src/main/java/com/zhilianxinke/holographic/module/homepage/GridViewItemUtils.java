package com.zhilianxinke.holographic.module.homepage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.module.homepage.adapter.HomepageAdapter;
import com.zhilianxinke.holographic.play.VideoViewPlayingActivity;
import com.zhilianxinke.holographic.utils.NetworkInfoManager;
import com.zhilianxinke.holographic.utils.dialog.SweetAlertDialog;

/**
 * Created by Ldb on 2016/1/27.
 */
public class GridViewItemUtils {


    /**
     * 播放或是下载
     **/
    public static void palyOrDownLoad(final Context context, final HomepageAdapter adapter, final AppVideoInfo appVideoInfo) {
        if (appVideoInfo.getIsDownload()) {
            Intent intent = new Intent(context, VideoViewPlayingActivity.class);
            intent.setData(Uri.parse( appVideoInfo.getLoacPath()));
           // intent.putExtra("playPath", appVideoInfo.getLoacPath());
            context.startActivity(intent);
        } else {
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(context.getString(R.string.this_video_no_download))
                    .setContentText(context.getString(R.string.download_success_look))
                    .setCancelText(context.getString(R.string.cancel))
                    .setConfirmText(context.getString(R.string.download))
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

                            if (!NetworkInfoManager.isWifi(context)) {
                                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(context.getString(R.string.warning))
                                        .setContentText(context.getString(R.string.move_notework))
                                        .setCancelText(context.getString(R.string.cancel))
                                        .setConfirmText(context.getString(R.string.ok)
                                        )
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
                                                adapter.updateItemData(appVideoInfo,true);
                                            }
                                        })
                                        .show();
                            } else {
                                adapter.updateItemData(appVideoInfo,true);
                            }

                        }
                    })
                    .show();
        }
    }

}
