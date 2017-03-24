package com.lvqingyang.emptyhand.Picture;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.lvqingyang.emptyhand.Base.BaseActivity;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.MyOkHttp;
import com.lvqingyang.emptyhand.Tools.MySweetAlertDialog;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lvqingyang.emptyhand.Picture.AppConstants.drawableToBitmap;


public class DetailActivity extends BaseActivity {

    //View
    private ImageView mImageView;
    private TextView mDayTv;
    private TextView mMonthTv;
    private TextView mTitleTv;
    private TextView mPhotoerTv;
    private TextView mContentTv;
    private CoordinatorLayout mCoordinatorLayout;
    private CardView mCardView;

    //Data
    private String mImgUrl,mTitle;
    private static final String KEY_URL = "URL";
    private static final String TAG = "DetailActivity";

    public static void start(Context context, String url) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra(KEY_URL, url);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initeView();
        final MySweetAlertDialog dialog=new MySweetAlertDialog(this);
        dialog.loading(null);

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String responce = MyOkHttp.getInstance()
                    .run(getIntent().getStringExtra(KEY_URL));
                    subscriber.onNext(responce);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String responce) {
                        Log.d(TAG, "onNext: " + responce);
                        if (responce != null) {
                            Document doc = Jsoup.parse(responce);
                            Element detailMain = doc.getElementsByClass("detail_text_main").first();

                            mTitle= detailMain.select("h2").text().substring(5);
                            setText(mTitleTv,mTitle);
                            StringBuffer sb = new StringBuffer();
                            String date = sb.append(detailMain.select("li.r_float").text()).delete(0, 5).toString();
                            String[] ymd = date.split("-");
                            setText(mMonthTv,ymd[1]);
                            setText(mDayTv,ymd[2]);

                            Element detail=detailMain.select("div.detail_text").first();
                            setText(mPhotoerTv,detailMain.select("div").last().text().replace("，你来掌镜",""));
                            Log.d(TAG, "onNext: "+detail.select("div")+"\n-------------------------"+
                                    detail.select("div").first()+"\n----------------------------"+detail.select("div").get(0));
                            String content=detail.select("div").get(0).text();
                            setText(mContentTv,content.substring(0,content.indexOf("摄影：")));

                            mImgUrl=detail.select("img").attr("abs:src");
                            Log.d(TAG, "onNext: "+mImgUrl);
                            Glide.with(DetailActivity.this)
                                    .load(mImgUrl)
                                    .into(new GlideDrawableImageViewTarget(mImageView) {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                            super.onResourceReady(resource, animation);
                                            mCardView.setVisibility(View.VISIBLE);
                                            ColorArt colorArt = new ColorArt(drawableToBitmap(mImageView.getDrawable()));
                                            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mCoordinatorLayout, "backgroundColor"
                                                    , colorArt.getBackgroundColor());
                                            objectAnimator.setEvaluator(new ArgbEvaluator());
                                            ObjectAnimator objectAnimator1 = ObjectAnimator.ofInt(mTitleTv, "textColor"
                                                    , colorArt.getDetailColor());
                                            objectAnimator1.setEvaluator(new ArgbEvaluator());
                                            ObjectAnimator objectAnimator2 = ObjectAnimator.ofInt(mMonthTv, "textColor"
                                                    , colorArt.getPrimaryColor());
                                            objectAnimator2.setEvaluator(new ArgbEvaluator());
                                            ObjectAnimator objectAnimator3 = ObjectAnimator.ofInt(mDayTv, "textColor"
                                                    , colorArt.getSecondaryColor());
                                            objectAnimator3.setEvaluator(new ArgbEvaluator());
                                            AnimatorSet set = new AnimatorSet();
                                            set.play(objectAnimator)
                                                    .with(objectAnimator1)
                                                    .with(objectAnimator2)
                                                    .with(objectAnimator3);
                                            set.setDuration(1000);
                                            set.start();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCompleted() {
                        dialog.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        dialog.error();
                        e.printStackTrace();
                    }
                });
    }


    private void initeView() {
//        initToolbar();
        mImageView = (AppCompatImageView) findViewById(R.id.img);
        mTitleTv = (TextView) findViewById(R.id.titlt);
        mMonthTv = (TextView) findViewById(R.id.month);
        mDayTv = (TextView) findViewById(R.id.day);
        mPhotoerTv = (TextView) findViewById(R.id.photoer);
        mContentTv = (TextView) findViewById(R.id.content);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.fristLayout);
        mCardView = (CardView) findViewById(R.id.td_header);
        mCardView.setVisibility(View.GONE);
       findViewById(R.id.ll)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, getString(R.string.long_click), Toast.LENGTH_SHORT).show();
                    }
                });

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                download(mImgUrl);
                return true;
            }
        });
    }


    private void setText(TextView textView,String text){
        textView.setText(text);
        TypefaceUtils.setTypeface(textView);
    }
}
