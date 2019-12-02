package com.rlcc.videolibrary.listener;


/**
 * The interface On gesture volume listener.
 *
 * @author robin  date 2017/11/3 Created by yangc E-Mail:liu_bin_0129@163.com Deprecated: 手势操作信息回调接口
 */
public interface OnGestureVolumeListener {
    /**
     * 改变声音
     *
     * @param mMax      d最大音量
     * @param currIndex 当前音量
     */
    void setVolumePosition(int mMax, int currIndex);


}
