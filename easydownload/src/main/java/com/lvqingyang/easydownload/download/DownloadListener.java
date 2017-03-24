package com.lvqingyang.easydownload.download;

/**
 * Author：LvQingYang
 * Date：2016/12/9
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public interface DownloadListener {
    //显示下载进度
    void onProgress(int progress);
    //下载成功
    void onSuccess();
    //下载失败
    void onFailed();
    //暂停下载
    void onPaused();
    //取消下载
    void onCanceled();
    //已经存在，重复下载
    void onExisted();
}
