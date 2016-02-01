package com.zhilianxinke.holographic.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.zhilianxinke.holographic.R;

/**
 * Created by Ldb on 2015/12/31.
 */
public class PopWindow {

    public PopWindow() {
    }

    private Context c;
    private int layout;
    private String URL;

    public PopWindow(Context c, int layout) {
        this.c = c;
        this.layout = layout;
        this.URL = URL;
        initPopupWindow(c, layout);
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private View parentView;


    /**
     * 弹出菜单 拍照，相册
     **/
    public void initPopupWindow(final Context c, int layout) {

        parentView = LayoutInflater.from(c).inflate(layout, null);
        pop = new PopupWindow(c);
        View view = LayoutInflater.from(c).inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        view.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        view.findViewById(R.id.item_popupwindows_Photo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri url = Uri.parse(PopWindow.this.URL);
                intent.setData(url);
                c.startActivity(intent);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    public void show() {
        ll_popup.startAnimation(AnimationUtils.loadAnimation(c, R.anim.activity_translate_in));
        pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }


}
