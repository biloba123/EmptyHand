package com.lvqingyang.emptyhand.Tools;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.lvqingyang.emptyhand.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Author：LvQingYang
 * Date：2017/1/13
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 * 封装对话框
 */

public class MySweetAlertDialog {
    private SweetAlertDialog mSweetAlertDialog;
    private Activity mActivity;
    private static final String TAG = "MySweetAlertDialog";

    public MySweetAlertDialog(Activity activity){
            Log.d(TAG, "MySweetAlertDialog: ");
            mSweetAlertDialog=new SweetAlertDialog(activity);
            mActivity=activity;
    }

    public void loading(String title){
        if (mSweetAlertDialog != null) {
            Log.d(TAG, "loading: ");
            mSweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
            mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mSweetAlertDialog.setTitleText(title==null?"Loading...":title);
            mSweetAlertDialog.setCanceledOnTouchOutside(false);
            mSweetAlertDialog.setCancelable(false);
            mSweetAlertDialog.show();
        }
    }

    public void success(){
        if (mSweetAlertDialog != null&&mSweetAlertDialog.isShowing()) {
            Log.d(TAG, "success: ");
            mSweetAlertDialog.setTitleText("Complete!")
                    .setConfirmText(mActivity.getString(R.string.get))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setCanceledOnTouchOutside(true);
            mSweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
    }

    public void complete(){
        if (mSweetAlertDialog != null&&mSweetAlertDialog.isShowing()) {
            mSweetAlertDialog.dismissWithAnimation();
        }
    }

    public void error(){
        if (mSweetAlertDialog != null&&mSweetAlertDialog.isShowing()) {
            mSweetAlertDialog.setTitleText(mActivity.getString(R.string.Error))
                    .setContentText(mActivity.getString(R.string.check_internet))
                    .setConfirmText(mActivity.getString(R.string.back))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            if (mActivity != null) {
                                mActivity.finish();
                            }
                        }
                    })
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            mSweetAlertDialog.setCanceledOnTouchOutside(false);
        }
    }

    public SweetAlertDialog getSweetAlertDialog(){
        return mSweetAlertDialog;
    }

}
