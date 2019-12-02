package com.rlcc.videolibrary.listener;

import com.google.android.exoplayer2.upstream.DataSource;

/**
 * The interface Data source listener.
 *
 * @author robin          date 2017/8/26         E-Mail:liu_bin_0129@163.com         Deprecated: 数据源工厂接口
 */
public interface DataSourceListener {
    /***
     * 自定义数据源工厂
     * @return DataSource.Factory data source factory
     */
    DataSource.Factory getDataSourceFactory();


}
