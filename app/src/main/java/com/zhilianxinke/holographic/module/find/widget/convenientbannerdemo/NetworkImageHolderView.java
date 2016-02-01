package com.zhilianxinke.holographic.module.find.widget.convenientbannerdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.module.find.widget.CBPageAdapter;


/**
 * Created by Sai on 15/8/4. 网络图片加载例子
 */
public class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
	private ImageView imageView;

	@Override
	public View createView(Context context) {
		// 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
		View view=LayoutInflater.from(context).inflate(R.layout.lunbo_item,null);
		imageView = (ImageView) view.findViewById(R.id.image);
		//imageView = new ImageView(context);
		//imageView.setScaleType(ImageView.ScaleType.CENTER);
		return view;
	}

	@Override
	public void UpdateUI(Context context, final int position, String data) {
		imageView.setImageResource(R.drawable.ic_default_adimage);
		new BitmapUtils(context).display(imageView, data);
		//ImageLoader.getInstance().displayImage(data, imageView);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// 点击事件
				/*Toast.makeText(view.getContext(), "点击了第" + position + "个",
						Toast.LENGTH_SHORT).show();*/
			}
		});
	}
}
