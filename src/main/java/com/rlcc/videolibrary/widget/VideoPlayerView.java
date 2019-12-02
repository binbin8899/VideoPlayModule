package com.rlcc.videolibrary.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.MediaStore;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.rlcc.utils.PermissionsApply;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import com.rlcc.utils.ToastUtils;
import com.rlcc.videolibrary.R;
import com.rlcc.videolibrary.bean.Episode;
import com.rlcc.videolibrary.listener.ExoPlayerViewListener;
import com.rlcc.videolibrary.utils.AnimUtils;
import com.rlcc.videolibrary.utils.VideoPlayUtils;
import com.rlcc.videolibrary.video.ExoDataBean;
import com.rlcc.videolibrary.video.ExoUserPlayer;

/**
 * author yangc
 * date 2017/7/21
 * E-Mail:liu_bin_0129@163.com
 * Deprecated: 视频播放video
 */
@TargetApi(16)
public final class VideoPlayerView extends BaseView {

    /**
     * Instantiates a new Video player view.
     *
     * @param context the context
     */
    public VideoPlayerView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Video player view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public VideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Video player view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiClickView();
    }

    /***
     * 初始化点击事件
     * **/
    private void intiClickView() {
        if (playerView.findViewById(R.id.exo_player_replay_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_replay_btn_id).setOnClickListener(onClickListener);
        }
        if (playerView.findViewById(R.id.exo_player_error_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_error_btn_id).setOnClickListener(onClickListener);
        }
        if (playerView.findViewById(R.id.exo_player_btn_hint_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_btn_hint_btn_id).setOnClickListener(onClickListener);
        }
        getSwitchText().setOnClickListener(onClickListener);
        exoControlsBack.setOnClickListener(onClickListener);
        playerView.findViewById(R.id.exo_video_fullscreen).setOnClickListener(onClickListener);
        if (isListPlayer() && !isLand()) {
            exoControlsBack.setVisibility(GONE);
        }
        playerView.setControllerVisibilityListener(visibilityListener);
        getSpeedText().setOnClickListener(onClickListener);
        getSkipTitleText().setOnClickListener(onClickListener);

        getSelectionText().setOnClickListener(onClickListener);
//        getMoreImage().setOnClickListener(onClickListener);
//        getShareImage().setOnClickListener(onClickListener);
        getNextEpisode().setOnClickListener(onClickListener);
        controllerView.setAnimatorListener(animatorListener);
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionsApply.checkAndRequestPermission((Activity) getContext(),PERMISSIONS_STORAGE);
        if(exoControllerRight!=null){
            exoControllerRight.findViewById(R.id.exo_player_screenshot).setOnClickListener(new ScreenshotListener());
        }
    }

    class ScreenshotListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            try {
                Bitmap bitmap = getPlayerView().shoot();
                if (bitmap == null) {
                    return;
                }
                String shootFile = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, Calendar.getInstance().getTimeInMillis() + "", "");
                getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(shootFile))));
                if (!shootFile.isEmpty()) {
                    Toast.makeText(getContext(), "截图保存到相册成功", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "截图保存到相册失败", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        ExoDataBean bean = new ExoDataBean(superState);
        bean.setLand(isLand());
        bean.setSetSystemUiVisibility(setSystemUiVisibility);
        bean.setSwitchIndex(switchIndex);
        bean.setNameSwitch(getNameSwitch());
        return superState;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (isLand()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state instanceof ExoDataBean) {
            ExoDataBean bean = (ExoDataBean) state;
            if (bean.getNameSwitch() != null) {
                setNameSwitch(bean.getNameSwitch());
            }
            setLand(bean.isLand());
            setSystemUiVisibility = bean.getSetSystemUiVisibility();
            switchIndex = bean.getSwitchIndex();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mExoPlayerListener != null) {
            mExoPlayerListener.onDetachedFromWindow(isListPlayer());
        }
    }


    /***
     * 设置是横屏,竖屏
     *
     * @param island  是否横屏
     */
    public void doOnConfigurationChanged(boolean island) {
        //横屏
        if (island) {
            if (isWGh()) {
                getPlayerView().getVideoSurfaceView().doOnConfigurationChanged(270);
            }
            VideoPlayUtils.hideActionBar(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                activity.getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            //判断是否开启多线路支持
            if (isShowVideoSwitch()) {
                TextView switchText = getSwitchText();
                switchText.setVisibility(VISIBLE);
                if (switchText.getText().toString().isEmpty() && !getNameSwitch().isEmpty()) {
                    switchText.setText(getNameSwitch().get(switchIndex));
                }
            }
            mLockControlView.setLockCheck(false);
            //列表显示
            showListBack(VISIBLE);
            //显示锁屏按钮
            showLockState(VISIBLE);
            //显示截图按钮
            //显示全屏按钮
            showFullscreenView(GONE);
            //横屏隐藏小进度条
            getTimeBar().setVisibility(GONE);
            //横屏显示大进度条
            getLandTimeBar().setVisibility(VISIBLE);
            //显示时间分隔符
            getMediaTimeSpan().setVisibility(VISIBLE);
            //横屏显示占位空间
            getLandWeight().setVisibility(VISIBLE);
            //显示倍速
            getSpeedText().setVisibility(VISIBLE);
            //显示跳过片头
            getSkipTitleText().setVisibility(VISIBLE);
            //显示集选
            getSelectionText().setVisibility(VISIBLE);
            //显示下一集按钮
            if (episodes != null && episodes.size() != 0 && getSelectionIndex() < episodes.size()) {
                getNextEpisode().setVisibility(VISIBLE);
            } else {
                getNextEpisode().setVisibility(GONE);
            }

        } else {//竖屏
            if (isWGh()) {
                getPlayerView().getVideoSurfaceView().doOnConfigurationChanged(0);
            }
//            activity.getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility);
            //状态栏显示
            VideoPlayUtils.showActionBar(activity);
            //多线路支持隐藏
            getSwitchText().setVisibility(GONE);
            //列表播放
            showListBack(GONE);
            //隐藏锁屏按钮移除
            showLockState(GONE);
            //显示截图按钮
            //显示全屏按钮
            showFullscreenView(VISIBLE);
            //横屏显示小进度条
            getTimeBar().setVisibility(VISIBLE);
            //横屏隐藏大进度条
            getLandTimeBar().setVisibility(GONE);
            //显时时间分隔符
            getMediaTimeSpan().setVisibility(GONE);
            //横屏显示占位空间
            getLandWeight().setVisibility(GONE);
            //隐藏倍速
            getSpeedText().setVisibility(GONE);
            //隐藏跳过片头
            getSkipTitleText().setVisibility(GONE);
            //隐藏集选
            getSelectionText().setVisibility(GONE);
            //隐藏下一集按钮
            getNextEpisode().setVisibility(GONE);
        }
        //显更改全屏按钮选中，自动旋转屏幕
//        getExoFullscreen().setChecked(island);
        setLand(island);
        scaleLayout();
        if (getPlaybackControlView().isPlaying()) {
            getPlaybackControlView().setOutAnim();
        }
    }


    /***
     * 获取监听事件  此方法不是外部调用。
     * @return ComponentListener component listener
     */
    public ExoPlayerViewListener getComponentListener() {
        return exoPlayerViewListener;
    }

    /****
     * 监听返回键
     ***/
    private void exitFullView() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getExoFullscreen().setChecked(false);
        doOnConfigurationChanged(false);
    }


    /****
     * 控制类显示隐藏监听
     ***/
    private PlayerControlView.VisibilityListener visibilityListener = new PlayerControlView.VisibilityListener() {
        @Override
        public void onVisibilityChange(int visibility) {
            showBackView(visibility, false);
            showLockState(visibility);
            if (belowView != null && visibility == View.GONE) {
                belowView.dismissBelowView();
            }
        }
    };
    /****
     * 动画监听
     ***/
    private AnimUtils.AnimatorListener animatorListener = new AnimUtils.AnimatorListener() {
        @Override
        public void show(boolean isIn) {
            mLockControlView.updateLockCheckBox(isIn);
            if (isIn) {
                if (isLand()) {
                    showLockState(VISIBLE);
                }
//                AnimUtils.setInAnim(exoControlsBack).start();
            } else {
//                AnimUtils.setOutAnim(exoControlsBack, false).start();
            }

        }
    };
    /***
     * 点击事件监听
     */
    private OnClickListener onClickListener = new OnClickListener() {

        public void onClick(View v) {
            if (v.getId() == R.id.exo_video_fullscreen ) {
                if (isVerticalFullScreen()) {//自定义实现全屏
                    doOnConfigurationChanged(!isLand());//横竖屏切换
                } else {
                    //切竖屏portrait screen
                    if (VideoPlayUtils.isLand(getContext())) {
                        //  doOnConfigurationChanged(false);
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        //切横屏landscape
                    } else {
                        //  doOnConfigurationChanged(true);
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }
            } else if (v.getId() == R.id.exo_controls_back) {
                if (isLand()) {
                    exitFullView();
                } else {
                    activity.onBackPressed();
                }
            } else if (v.getId() == R.id.exo_player_error_btn_id) {
                onCreatePlayer();
            } else if (v.getId() == R.id.exo_player_replay_btn_id) {
                onCreatePlayer();
                //切换
            } else if (v.getId() == R.id.exo_video_switch) {
                if (belowView == null) {
                    belowView = new BelowView(getContext(), getNameSwitch());
                    belowView.setOnItemClickListener(new BelowView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, String name) {
                            if (mExoPlayerListener != null) {
                                mExoPlayerListener.switchUri(position);
                            }
                            getSwitchText().setText(name);
                            belowView.dismissBelowView();
                        }
                    });
                }
                //隐藏控制VIEW
                getPlaybackControlView().setOutAnim();
                belowView.showBelowView(v, true, getSwitchIndex());
                //提示播放
            } else if (v.getId() == R.id.exo_player_btn_hint_btn_id) {
                showBtnContinueHint(View.GONE);
                if (mExoPlayerListener != null) {
                    mExoPlayerListener.playVideoUri();
                }
            }else if (v.getId() == R.id.exo_speed) {
                //倍速
                mSpeedView = new SpeedView(activity, null);
                mSpeedView.setOnItemClickListener(new SpeedView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, String name) {
                        float speedValue = Float.parseFloat(name.replace("x", "").trim());
                        mExoPlayerListener.getPlay().setPlaybackParameters(speedValue);
                        getSpeedText().setText(name);
                        getSpeedText().setTag(speedValue);
                        VideoPlayerView.this.setSpeedIndex(position);
                        mSpeedView.dismissView();
                    }
                });
                //隐藏控制VIEW
                getPlaybackControlView().setOutAnim();
                mSpeedView.showBelowView(v, true, VideoPlayerView.this.getSpeedIndex());
            } else if (v.getId() == R.id.exo_artwork) {
                //分享H5地址
//                if (true) {
//                    ToastUtils.showLongToastSafe("敬请期待");
//                    return;
//                }
//                if (TextUtils.isEmpty(shareVideoH5)) {
//                    return;
//                }
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
////                intent.putExtra(Intent.EXTRA_TEXT, "这是要分享的消息");
//                intent.putExtra(Intent.EXTRA_TEXT, shareVideoH5);
//                getContext().startActivity(intent);
            } else if (v.getId() == R.id.exo_video_selection) {
                //选集
                mSelectionView = new SelectionView(activity, episodes);
                mSelectionView.setOnItemClickListener(new SelectionView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, String name) {
                        for (int i = 0; i < episodes.size(); i++) {
                            Episode e = episodes.get(i);
                            if (i == position) {
                                e.isSelect = true;
                            } else {
                                e.isSelect = false;
                            }
                        }
                        VideoPlayerView.this.setSelectionIndex(position);
                        if(mPLayEpisodeListener!=null) {
                            mPLayEpisodeListener.playEpisode(episodes.get(position));
                        }
                        if (position < episodes.size() - 1) {
                            getNextEpisode().setVisibility(VISIBLE);
                        } else {
                            getNextEpisode().setVisibility(GONE);
                        }
                        mSelectionView.dismissView();
                    }
                });
                //隐藏控制VIEW
                getPlaybackControlView().setOutAnim();
                mSelectionView.showView(v, true, getSelectionIndex());
            } else if (v.getId() == R.id.exo_artwork) {
                ToastUtils.showLongToastSafe("敬请期待");
//                //更多
//                mMoreView = new MoreView(activity, isFaver);
//                mMoreView.setOnClickListener(new MoreView.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (view.getId() == R.id.favorites) {
//                            if (mPlayFavorites != null) {
//                                boolean faver = (boolean) view.getTag();
//                                mPlayFavorites.favor(faver);
//                            }
//                        }
//                    }
//                });
//                mMoreView.showView(v, true, getSelectionIndex());
            } else if (v.getId() == R.id.skip_title) {
                ToastUtils.showLongToastSafe("敬请期待");
            } else if (v.getId() == R.id.exo_next_episode) {
                //下一集
//                int index = selectionIndex + 1;
//                if (index >= episodes.size() - 1) {
//                    getNextEpisode().setVisibility(GONE);
//                }
//                for (SubsetModel.Episode se : episodes) {
//                    se.isSelect = false;
//                }
//                episodes.get(index).isSelect = true;
//                VideoPlayerView.this.setSelectionIndex(index);
//                mPlayEpisode.playEpisode(episodes.get(index));
            }
        }
    };

    private void onCreatePlayer() {
        if (VideoPlayUtils.isNetworkAvailable(getContext())) {
            showErrorState(View.GONE);
            showReplay(View.GONE);
            if (mExoPlayerListener != null) {
                mExoPlayerListener.onCreatePlayers();
            }

        } else {
            Toast.makeText(getContext(), R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 控制类监听类
     **/
    private final ExoPlayerViewListener exoPlayerViewListener = new ExoPlayerViewListener() {

        @Override
        public void onDestroy() {
            VideoPlayerView.this.onDestroy();
        }

        @Override
        public void setPlayer(SimpleExoPlayer simpleExoPlayer) {
            playerView.setPlayer(simpleExoPlayer);
        }

        @Override
        public void startPlayer(ExoUserPlayer exoUserPlayer) {
            Object position = getTag();
            if (isListPlayer() && position != null) {
                if (tags.get(position.toString()) != null && tags2.get(position.toString()) != null) {
                    int positions = tags.get(position.toString()).intValue();
                    int index = tags2.get(position.toString());
                    exoUserPlayer.setPosition(index, positions);
                    tags.remove(position.toString());
                    tags2.remove(position.toString());
                }
            }
        }

        @Override
        public void showAlertDialog() {
            showDialog();
        }

        @Override
        public void onResumeStart() {
            if (isListPlayer()) {
                setPlayerBtnOnTouch(true);
            } else {
                if (mExoPlayerListener != null) {
                    mExoPlayerListener.onCreatePlayers();
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onPrepared() {
            playerView.setOnTouchListener(mOnTouchListener);
        }

        @Override
        public void showLoadStateView(int visibility) {
            showLoadState(visibility);
        }

        @Override
        public void showReplayView(int visibility) {
            showReplay(visibility);

        }

        @Override
        public void showErrorStateView(int visibility) {
            showErrorState(visibility);
        }

        @Override
        public void showNetSpeed(final String netSpeed) {
            if (isLoadingLayoutShow()) {
                playerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (videoLoadingShowText != null) {
                            videoLoadingShowText.setText(netSpeed);
                        }
                    }
                });
            }
        }

        @Override
        public void onConfigurationChanged(boolean isLand) {
            doOnConfigurationChanged(isLand);
        }

        @Override
        public void showGestureView(int visibility) {
            mGestureControlView.showGesture(visibility);
        }

        @Override
        public void setTimePosition(@NonNull SpannableString seekTime) {
            mGestureControlView.setTimePosition(seekTime);
        }

        @Override
        public void setVolumePosition(int mMaxVolume, int currIndex) {
            mGestureControlView.setVolumePosition(mMaxVolume, currIndex);
        }

        @Override
        public void setBrightnessPosition(int mMaxVolume, int currIndex) {
            mGestureControlView.setBrightnessPosition(mMaxVolume, currIndex);
        }

        @Override
        public void setUseController(boolean useController) {
            getPlayerView().setUseController(useController);
        }

        @Override
        public void toggoleController(boolean isShowFull, boolean isShow) {
            if (isShow) {
                playerView.showController();
                getPlaybackControlView().setInAnim();
                setControllerHideOnTouch(true);
            } else {
                getPlaybackControlView().setOutAnim();
                setControllerHideOnTouch(false);
            }
        }

        @Override
        public void setControllerHideOnTouch(boolean onTouch) {
            getPlayerView().setControllerHideOnTouch(onTouch);
        }

        @Override
        public void showPreview(int visibility, boolean isPlayer) {
            if (!isPlayer) {
                showPreViewLayout(visibility);
                showBottomView(GONE);
                getPreviewImage().setVisibility(visibility);

            } else {
                if (exoPreviewPlayBtn != null) {
                    exoPreviewPlayBtn.setVisibility(GONE);
                }
            }
        }

        @Override
        public void setPlayerBtnOnTouch(boolean isTouch) {
            if (isTouch) {
                getPlaybackControlView().getPlayButton().setOnTouchListener(onTouchListener);
                if (exoPreviewPlayBtn != null) {
                    exoPreviewPlayBtn.setOnTouchListener(onTouchListener);
                }
            } else {
                getPlaybackControlView().getPlayButton().setOnTouchListener(null);
                if (exoPreviewPlayBtn != null) {
                    exoPreviewPlayBtn.setOnTouchListener(null);
                }
            }

        }

        @Override
        public void reset() {
            resets();
            exoPlayerViewListener.showPreview(VISIBLE, false);
        }

        @Override
        public int getHeight() {
            return playerView == null ? 0 : playerView.getHeight();
        }

        @Override
        public void setShowWitch(boolean showVideoSwitch) {
            setShowVideoSwitch(showVideoSwitch);
        }

        @Override
        public void setSeekBarOpenSeek(boolean isOpenSeek) {
            getTimeBar().setOpenSeek(isOpenSeek);
            getLandTimeBar().setOpenSeek(isOpenSeek);
            setPlayerGestureOnTouch(isOpenSeek);
        }

        @Override
        public void setOpenSeek(boolean openSeek) {
            getTimeBar().setOpenSeek(openSeek);
            getLandTimeBar().setOpenSeek(openSeek);
        }

        @Override
        public void exitFull() {
            exitFullView();
        }

        @Override
        public void setSwitchName(@NonNull List<String> name, int switchIndex) {
            VideoPlayerView.this.setSwitchName(name, switchIndex);
        }

        @Override
        public void setTag(Integer position) {
            VideoPlayerView.this.setTag(position.toString());
        }

    };



}
