package com.rlcc.videolibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rlcc.videolibrary.R;
import com.rlcc.videolibrary.listener.VideoWindowListener;
import com.rlcc.videolibrary.utils.AnimUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdControlView extends FrameLayout {

    private View exoAdTime, exoAdImage;
    TextView adsTxt;
    BaseView baseView;
    public AdControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,final BaseView baseView) {
        super(context, attrs, defStyleAttr);
        this.baseView = baseView;
        int timeId = R.layout.simple_exo_ad_view;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VideoPlayerView, 0, 0);
            try {
                timeId = a.getResourceId(R.styleable.VideoPlayerView_ad_layout_id, timeId);
//                errorId = a.getResourceId(R.styleable.VideoPlayerView_player_error_layout_id, errorId);
//                playerHintId = a.getResourceId(R.styleable.VideoPlayerView_player_hint_layout_id, playerHintId);
            } finally {
                a.recycle();
            }
        }
        exoAdTime = inflate(context, timeId, null);
//        exoAdImage = inflate(context, replayId, null);
        exoAdTime.setVisibility(VISIBLE);
//        playReplayLayout.setVisibility(GONE);
//        playBtnHintLayout.setVisibility(GONE);
        addView(exoAdTime, getChildCount());
//        addView(playReplayLayout, getChildCount());
//        addView(playBtnHintLayout, getChildCount());
        adsTxt = findViewById(R.id.ads);
        setVisibility(GONE);
    }

    public void showView(){
        baseView.mExoPlayerListener.getPlay().addOnWindowListener(new VideoWindowListener() {
            @Override
            public void onCurrentIndex(int currentIndex, int windowCount) {
                if(currentIndex==0&&windowCount>1){
                    AdControlView.this.setVisibility(VISIBLE);
                    baseView.getPlayWatermark().setVisibility(GONE);
                    //禁用手执
                    baseView.setPlayerGestureOnTouch(false);
                }else{
                    AdControlView.this.setVisibility(GONE);
                    baseView.getPlayWatermark().setVisibility(VISIBLE);
                    //手执可用
                    baseView.setPlayerGestureOnTouch(true);
                }
            }
        });
        baseView.getPlaybackControlView().addUpdateProgressListener(new AnimUtils.UpdateProgressListener() {
            @Override
            public void updateProgress(long position, long bufferedPosition, long duration) {
                int s=(int) ((duration-position)/1000);
                adsTxt.setText(String.valueOf(s));
            }
        });
    }
}
