package com.rlcc.videolibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rlcc.videolibrary.R;
import com.rlcc.videolibrary.bean.Episode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

/**
 * author  liubin
 * date 2018/1/6
 * E-Mail:liu_bin_0129@163.com
 * Deprecated:
 */

public class SelectionAdapter extends BaseAdapter {

    private List<Episode> data;
    private Context mContext;
    private int selectIndex;
    private LayoutInflater mInflater;

    public SelectionAdapter(@NonNull Context context, @NonNull List<Episode> list) {
        this.data = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Episode getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppCompatTextView textView;
        if (convertView == null) {
            textView = (AppCompatTextView) mInflater.inflate(R.layout.simple_exo_selection_item, parent, false);
        } else {
            textView = (AppCompatTextView) convertView;
        }
        textView.setText(data.get(position).episode);
        if (position == selectIndex) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.simple_exo_color_switch_item));
//            textView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_pw_selection));
        } else {
            textView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
//            textView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.list_view_back));
        }
        return textView;
    }

    /***
     * 设置选中索引
     * @param selectIndex selectIndex
     * ***/
    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }
}
