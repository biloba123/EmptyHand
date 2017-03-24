package com.lvqingyang.emptyhand.Picture;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.lvqingyang.emptyhand.Base.BaseFragment;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.Address;
import com.lvqingyang.emptyhand.Tools.MyOkHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lvqingyang.emptyhand.Picture.AppConstants.drawableToBitmap;

/**
 * Author：LvQingYang
 * Date：2017/2/2
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class PictureFragment extends BaseFragment{
    //View
    private android.support.v7.widget.AppCompatImageView img;
    private TextView title;
    private TextView text;
    private TextView mDateText;
    private RelativeLayout mRelativeLayout;
    //Data
    private Picture mPicture;
    private static final String KEY_PIC = "PIC";
    private static final String TAG = "PictureFragment";

    public static PictureFragment newInstance() {

        Bundle args = new Bundle();

        PictureFragment fragment = new PictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_picture;
    }

    @Override
    protected void initeView(View view) {
        this.text = (TextView) view.findViewById(R.id.text);
        this.title = (TextView) view.findViewById(R.id.title);
        this.img = (AppCompatImageView) view.findViewById(R.id.img);
        mDateText = (TextView) view.findViewById(R.id.date_text);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.picture_fragment);
        view.findViewById(R.id.show_detail_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.start(getActivity(), mPicture.getDetailUrl());
            }
        });
    }

    @Override
    protected void initeData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String > subscriber) {
                try {
                    String responce= MyOkHttp.getInstance().run(Address.pictureUrl);
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
                    public void onNext(String  responce) {
                        Log.d(TAG, "onNext: "+responce);

                        Document doc = Jsoup.parseBodyFragment(responce);
                        Element body = doc.body();
                        Elements pics=body.getElementsByClass("ajax_list");
                        Element pic=pics.first();
                        mPicture=new Picture();
                        mPicture.setImgUrl(pic.select("img").attr("abs:src"));
                        mPicture.setTitle(pic.select("a[href]").text().substring(5));
                        mPicture.setText(pic.select("dd.ajax_dd_text").text());
                        mPicture.setDetailUrl("http://m.nationalgeographic.com.cn"+
                                pic.getElementsByTag("dt").select("a").attr("href"));
                    }

                    @Override
                    public void onCompleted() {
                        showPicture();
                        showContent();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e );
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                        cameError();
                    }
                });
    }

    private void showPicture(){
        Glide.with(getActivity())
                .load(mPicture.getImgUrl())
                .into(new GlideDrawableImageViewTarget(img) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        ColorArt colorArt = new ColorArt(drawableToBitmap(img.getDrawable()));
                        ObjectAnimator objectAnimator=ObjectAnimator.ofInt(mRelativeLayout,"backgroundColor",
                                colorArt.getBackgroundColor())
                                .setDuration(1000);
                        objectAnimator.setEvaluator(new ArgbEvaluator());
                        objectAnimator.start();
                    }
                });
        setText(text, mPicture.getText());
        setText(title, mPicture.getTitle());
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("MM月dd号");
        setText(mDateText,format.format(date)+"的 图");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        Log.d(TAG, "onSaveState: "+mPicture.getImgUrl());
        outState.putParcelable(KEY_PIC, mPicture);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        mPicture=savedInstanceState.getParcelable(KEY_PIC);
        if (mPicture == null) {
            Log.d(TAG, "onRestoreState: null");
        }
        showPicture();
    }
}
