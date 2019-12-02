package com.rlcc.videolibrary.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.rlcc.videolibrary.R;
import com.rlcc.videolibrary.utils.VideoPlayUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * author liubin
 * date 2017/7/19
 * E-Mail:liu_bin_0129@163.com
 * Deprecated: 倍速列表
 */
class SpeedView implements View.OnClickListener {
    private View convertView;
    private PopupWindow pw;
    private ListView listView;
    private OnItemClickListener onItemClickListener;
    private SwitchAdapter adapter;
    private boolean isDisabled;

    /**
     * Instantiates a new Below view.
     *
     * @param c        the c
     * @param listName the list name
     */
    public SpeedView(@NonNull Context c, @Nullable List<String> listName) {
        this.convertView = View.inflate(c, R.layout.simple_exo_speed, null);
        listView = convertView.findViewById(R.id.list_item);
        if (listName == null) {
            listName = new ArrayList<>(Arrays.asList(c.getResources().getStringArray(R.array.exo_video_speed_text)));
        }
        adapter = new SwitchAdapter(c, listName);
        listView.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, parent.getAdapter().getItem(position) + "");
                }
            }
        });
        convertView.setOnClickListener(this);

    }

    /**
     * Show below view.
     *
     * @param view                   the view
     * @param canceledOnTouchOutside the canceled on touch outside
     * @param selectIndex            selectIndex
     */
    public void showBelowView(@NonNull View view, boolean canceledOnTouchOutside, int selectIndex) {
        if (pw == null) {
            int height = (int) (view.getResources().getDimension(R.dimen.dp30) * adapter.getCount());
            adapter.setSelectIndex(selectIndex);
            this.pw = new PopupWindow(convertView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
            this.pw.setOutsideTouchable(canceledOnTouchOutside);
            this.pw.setAnimationStyle(R.style.AnimationRightFade);
            if (onItemClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onItemClickListener != null && position != adapter.getSelectIndex()) {
                            onItemClickListener.onItemClick(position, adapter.getItem(position));
                            adapter.setSelectIndex(position);
//                            SpeedView.this.onClick(position);
                        }
                    }
                });
            }
        }
        try {
            //解决PopupWindow无法覆盖状态栏
            Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
            mLayoutInScreen.setAccessible(true);
            mLayoutInScreen.set(pw, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        VideoPlayUtils.hideActionBar(convertView.getContext());

        //防止导航栏显示
        pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        pw.showAtLocation(view, Gravity.CENTER, location[0] - view.getWidth() / 6, location[1] - pw.getHeight());
    }


    /**
     * Dismiss below view.
     */
    public void dismissView() {
        if (this.pw != null && pw.isShowing()) {
            this.pw.dismiss();
        }
    }

    /**
     * Sets on item click listener.
     *
     * @param onItemClickListener the on item click listener
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        Log.e("", v.getTag() + "");
        dismissView();
    }

    /**
     * The interface On item click listener.
     */
    public interface OnItemClickListener {
        /**
         * item 点击双股事件
         *
         * @param position 索引
         * @param name     名称
         */
        void onItemClick(int position, String name);
    }


    private class ComponentListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            SpeedView.this.onClick(view);
        }
    }

}
