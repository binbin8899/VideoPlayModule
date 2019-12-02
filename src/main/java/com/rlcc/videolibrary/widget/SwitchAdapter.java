package com.rlcc.videolibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rlcc.videolibrary.R;

import java.util.List;

/**
 * author  yangc
 * date 2018/1/6
 * E-Mail:liu_bin_0129@163.com
 * Deprecated:
 */

public class SwitchAdapter extends BaseAdapter {

    private List<String> data;
    private Context mContext;
    private int selectIndex;
    private LayoutInflater mInflater;

    SwitchAdapter(@NonNull Context context, @NonNull List<String> list) {
        this.data = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
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
            textView = (AppCompatTextView) mInflater.inflate(R.layout.simple_exo_belowview_item, parent, false);
        } else {
            textView = (AppCompatTextView) convertView;
        }
        textView.setText(data.get(position));
        if (position == selectIndex) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.simple_exo_color_switch_item));
        } else {
            textView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        }
        return textView;
    }

    /***
     * 设置选中索引
     * @param selectIndex selectIndex
     * ***/
      void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

      int getSelectIndex() {
        return selectIndex;
    }
}