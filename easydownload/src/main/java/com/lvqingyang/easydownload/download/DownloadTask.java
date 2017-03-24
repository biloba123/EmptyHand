package com.lvqingyang.easydownload.download;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author：LvQingYang
 * Date：2016/12/9
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class DownloadTask extends AsyncTask<String ,Integer,Integer> {

    //下载状态
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED=1;
    public static final int TYPE_PAUSED=2;
    public static final int TYPE_CANCELED=3;
    public static final int TYPE_EXIST=4;

    //回调DownloadListener以返回下载状态
    private DownloadListener mListener;

    //是否取消或暂停下载
    private boolean isCanceled=false;
    private boolean isPaused=false;

    //当前进度
    private int lastProgress;


    public DownloadTask(DownloadListener listener) {
        mListener = listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream is=null;
        RandomAccessFile savedFile=null;
        File file=null;

        try{
            //记录下载长度
            long downloadLength=0;
            String downloadUrl=strings[0];

            //保存文件名
            String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));

            //下载目录
            String directory= Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).getPath();


            file=new File(directory+fileName);
            if (file.exists()) {
                downloadLength=file.length();
            }

            long contentLength=getContentLength(downloadUrl);
            if (contentLength==0) {
                return TYPE_FAILED;
            }else if(contentLength==downloadLength) {
                return TYPE_EXIST;
            }

           OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder()
                    //断点下载，指定从哪开始
                    .addHeader("RANGE","bytes="+downloadLength+"-")
                    .url(downloadUrl)
                    .build();
            Response response=client.newCall(request).execute();

            if (response!=null) {
                is=response.body().byteStream();
                savedFile=new RandomAccessFile(file,"rw");
                savedFile.seek(downloadLength);
                byte[] b=new byte[1024];
                int total=0;
                int len;
                while ((len=is.read(b))!=-1){
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    }else if (isPaused) {
                        return TYPE_PAUSED;
                    }else {
                        total+=len;
                        savedFile.write(b,0,len);
                        //计算进度
                        int progress= (int) ((total+downloadLength)*100/contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled&&file!=null) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress=values[0];
        if (progress>lastProgress) {
            mListener.onProgress(progress);
            lastProgress=progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer) {
            case TYPE_SUCCESS:
                mListener.onSuccess();
                break;
            case TYPE_FAILED :
                mListener.onFailed();
                break;
            case TYPE_PAUSED :
                mListener.onPaused();
                break;
            case TYPE_CANCELED :
                mListener.onCanceled();
                break;
            case TYPE_EXIST:
                mListener.onExisted();
                break;
        }
    }

    public void pauseDownload(){
        isPaused=true;
    }

    public void cancelDownload(){
        isCanceled=true;
    }

    //计算下载文件大小
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response=client.newCall(request).execute();

        if (response!=null&&response.isSuccessful()) {
            long contentLength=response.body().contentLength();
            return contentLength;
        }
        return 0;
    }
}
