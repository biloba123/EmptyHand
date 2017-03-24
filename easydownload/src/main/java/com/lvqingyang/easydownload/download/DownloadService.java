package com.lvqingyang.easydownload.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.lvqingyang.easydownload.R;

import java.io.File;

public class DownloadService extends Service {

    private DownloadTask mDownloadTask;
    //下载地址
    private String mDownloadUrl;
    //是否在状态栏显示通知
    private boolean isNotificated;

    private DownloadListener mListener=new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification(getString(R.string.downloading),progress));
        }

        @Override
        public void onSuccess() {
            mDownloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification(getString(R.string.success),-1));
            Toast.makeText(DownloadService.this,getString(R.string.success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            mDownloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification(getString(R.string.fail),-1));
            Toast.makeText(DownloadService.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            mDownloadTask=null;
            Toast.makeText(DownloadService.this, getString(R.string.pause), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            mDownloadTask=null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, getString(R.string.cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onExisted() {
            mDownloadTask=null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, getString(R.string.exist), Toast.LENGTH_SHORT).show();
        }
    };

    public static void prepare(Context context, ServiceConnection connection) {
        Intent intent=new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent,connection,BIND_AUTO_CREATE);
    }

    private DownloadBinder mBinder=new DownloadBinder();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }


    public class DownloadBinder extends Binder{
        public void startDownload(String url){
//            Toast.makeText(DownloadService.this, getString(R.string.downloading), Toast.LENGTH_SHORT).show();
            if (mDownloadTask == null) {
                mDownloadUrl=url;
                mDownloadTask=new DownloadTask(mListener);
                mDownloadTask.execute(mDownloadUrl);
                startForeground(1,getNotification(getString(R.string.downloading),0));
            }
        }

        public void pauseDownload(){
            if (mDownloadTask != null) {
                mDownloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if (mDownloadTask != null) {
                mDownloadTask.cancelDownload();
            }else {
                if (mDownloadUrl != null) {
                    String fileName=mDownloadUrl.substring(mDownloadUrl.lastIndexOf("/"));

                    String directory= Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file=new File(directory+fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, getString(R.string.cancel), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title,int progress){
        //intent................
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.download)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
        .setContentTitle(title);
        if(progress>=0) {
            builder.setProgress(100,progress,false)
                    .setContentText(progress+"%");
        }
        return builder.build();
    }
}
