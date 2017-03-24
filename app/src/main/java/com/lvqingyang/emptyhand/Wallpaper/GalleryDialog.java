package com.lvqingyang.emptyhand.Wallpaper;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lvqingyang.emptyhand.Base.BaseActivity;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;

import java.io.IOException;

public class GalleryDialog extends DialogFragment implements View.OnClickListener {
    //Data
    private Wallpaper mWallpaper;
    private WallpaperManager mManager;
    private static final String KEY_PAGER = "PAGER";
    private static final String KEY_BG = "BG";
    private static final String KEY_TEXT = "TEXT";
    //View
    private TextView mTextView;
    private ImageView mImageView;
    private FloatingActionButton mWallpaperFab;
    private FloatingActionButton mDownloadFab;


    public static GalleryDialog newInstance(Wallpaper paper,int bgColor,int detailColor) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_PAGER,paper);
        args.putInt(KEY_BG,bgColor);
        args.putInt(KEY_TEXT,detailColor);
        GalleryDialog fragment = new GalleryDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog=getDialog();
        if (dialog != null) {
            dialog.getWindow()
                    .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(null);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mManager=WallpaperManager.getInstance(getActivity());
        mWallpaper= (Wallpaper) getArguments().getParcelable(KEY_PAGER);
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_gallery,null);
        mImageView = (ImageView) v.findViewById(R.id.image_view);
        mTextView = (TextView) v.findViewById(R.id.text_view);
        setText(mTextView,mWallpaper.getTitle());
        //FAB
        mWallpaperFab = (FloatingActionButton) v.findViewById(R.id.wallpaper_fab);
        mDownloadFab = (FloatingActionButton) v.findViewById(R.id.download_fab);
        mWallpaperFab.setOnClickListener(this);
        mDownloadFab.setOnClickListener(this);


        //加载图片
        Glide.with(this)
                .load(mWallpaper.getImgUrl())
                .into(mImageView);
        //设置颜色
        int bgColor=getArguments().getInt(KEY_BG),
                detailColor=getArguments().getInt(KEY_TEXT);
        if (bgColor!=-1&&detailColor!=-1) {
            ObjectAnimator objectAnimator=ObjectAnimator.ofInt(mTextView,"backgroundColor",
                    bgColor);
            objectAnimator.setEvaluator(new ArgbEvaluator());

            ObjectAnimator objectAnimator1=ObjectAnimator.ofInt(mTextView,"textColor",
                    detailColor);
            objectAnimator1.setEvaluator(new ArgbEvaluator());

            ObjectAnimator animator=ObjectAnimator.ofFloat(mWallpaperFab,"alpha",
                    0f,1f);
            objectAnimator.setEvaluator(new ArgbEvaluator());

            ObjectAnimator animator1=ObjectAnimator.ofFloat(mDownloadFab,"alpha",
                    0f,1f);

            AnimatorSet set=new AnimatorSet();
            set.play(objectAnimator)
                    .with(objectAnimator1)
                    .with(animator)
                    .with(animator1);
            set.setDuration(600);
            set.start();
        }


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    private void setText(TextView textView,String text){
        textView.setText(text);
        TypefaceUtils.setTypeface(textView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wallpaper_fab:
                Toast.makeText(getActivity(), getString(R.string.wallpaper_set),
                        Toast.LENGTH_SHORT).show();
                Glide.with(this)
                        .load(mWallpaper.getImgUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (mManager != null) {
                                    try {
                                        mManager.setBitmap(resource);
                                        Toast.makeText(getActivity(), getString(R.string.wallpaper_complete),
                                                Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        Toast.makeText(getActivity(), getString(R.string.wallpaper_error),
                                                Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                break;
            case R.id.download_fab:
                ((BaseActivity)getActivity()).download(mWallpaper.getImgUrl());
                break;
        }
    }

}
