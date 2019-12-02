package com.rlcc.videolibrary.listener;

/**
 * The interface Video window listener.
 * 如果自己在播放视频时特出处理。实现该接口回调
 *             视频切换回调处理，进行布局处理，控制布局显示
 * @author robin          date 2017/2/25         E-Mail:1007181167@qq.com         Description：多个视频接口
 */
public interface VideoWindowListener {


    /***
     * 返回当前位置
     * @param currentIndex 当前视频窗口索引
     * @param windowCount 总数
     */
    void onCurrentIndex(int currentIndex, int windowCount);

}
