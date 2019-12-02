package com.rlcc.videolibrary.listener;


import com.rlcc.videolibrary.video.ExoUserPlayer;

/**
 * The interface Exo player listener.
 *
 * @author robin          date 2017/7/21         E-Mail:liu_bin_0129@163.com         Deprecated: view回调控制类接口
 */
public interface ExoPlayerListener {

    /***
     * 播放控制类回调
     */
    void onCreatePlayers();

    /***
     *释放控制类
     */
    void replayPlayers();

    /***
     *选择多线路
     * @param position 索引
     */
    void switchUri(int position);

    /***
     *播放视频地址
     */
    void playVideoUri();

    /***
     *得到内核控制类
     */
    void onDetachedFromWindow(boolean isListPlayer);

    /***
     *播放视频
     */
    void startPlayers();

    void land();
    /***
     *得到内核控制类
     * @return ExoUserPlayer play
     */
    ExoUserPlayer getPlay();
}
