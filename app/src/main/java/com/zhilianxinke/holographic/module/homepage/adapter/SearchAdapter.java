package com.zhilianxinke.holographic.module.homepage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.entity.SearchHistory;

import java.util.List;

/**
 * Created by Ldb on 2016/1/25.
 */
public class SearchAdapter extends BaseAdapter {

    private List<SearchHistory> mTitleArray;// 标题列表
    private LayoutInflater inflater = null;
    private Context mContext;

    /**
     * Adapter构造方法
     *
     * @param titleArray
     */
    public SearchAdapter(Context context, List<SearchHistory> titleArray) {
        // TODO Auto-generated constructor stub
        this.mTitleArray = titleArray;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setmTitleArray(List<SearchHistory> mTitleArray) {
        this.mTitleArray = mTitleArray;
    }

    /**
     * 获取总数
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mTitleArray == null ? 0 : mTitleArray.size();
    }

    /**
     * 获取Item内容
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mTitleArray.get(position);
    }

    /**
     * 获取Item的ID
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 设置
        SearchHistory sh = (SearchHistory) getItem(position);
        if (sh.getClear() == 0) {
            holder.clear.setVisibility(View.VISIBLE);
            holder.clear.setText(sh.getSearchName());
            holder.titletv.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
        } else {
            holder.titletv.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.titletv.setText(sh.getSearchName());
            holder.time.setText(sh.getSearchTime());
            holder.clear.setVisibility(View.GONE);
        }
        return convertView;
    }


    /**
     * 刷新数据
     *
     * @param array
     */
    public void refreshData(List<SearchHistory> array) {
        this.mTitleArray = array;
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public final TextView titletv;
        public final TextView clear;
        public final TextView time;
        public final View root;

        public ViewHolder(View root) {
            titletv = (TextView) root.findViewById(R.id.title_tv);
            clear = (TextView) root.findViewById(R.id.clear);
            time = (TextView) root.findViewById(R.id.time);
            this.root = root;
        }
    }
}