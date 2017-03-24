package com.lvqingyang.emptyhand.Book;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.MySweetAlertDialog;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author：LvQingYang
 * Date：2017/2/5
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class BookDteailDialog extends DialogFragment implements View.OnClickListener {
    //Argument
    private static final String ARG_BOOK = "ARG_BOOK";
    //Data
    private BookInfo mBook;
    private ImageView mBookIv;
    private TextView mNameTv;
    private TextView mAuthorTv;
    private List<Chapter> mChapters;
    private TextView mDownloadTv;
    private TextView mCancelTv;

    //View

    public static BookDteailDialog newInstance(BookInfo b) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK,b);
        BookDteailDialog fragment = new BookDteailDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog=getDialog();
        if (dialog != null) {
            dialog.getWindow()
                    .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(null);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBook=getArguments().getParcelable(ARG_BOOK);

        final View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_book_detail,null);
        mBookIv = (ImageView) v.findViewById(R.id.book_image_view);
        mNameTv = (TextView) v.findViewById(R.id.name_text_view);
        mAuthorTv = (TextView) v.findViewById(R.id.author_text_view);
        mDownloadTv = (TextView) v.findViewById(R.id.down);
        mCancelTv = (TextView) v.findViewById(R.id.cancel);

        mDownloadTv.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);


        Glide.with(this)
                .load(mBook.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.bookPageBg)
                .into(mBookIv);
        TypefaceUtils.setText(mNameTv, mBook.getName());
        TypefaceUtils.setText(mAuthorTv,mBook.getAuthor());
        TypefaceUtils.setTypeface((TextView) v.findViewById(R.id.tv) );
        TypefaceUtils.setTypeface(mDownloadTv);
        TypefaceUtils.setTypeface(mCancelTv);

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String > subscriber) {
                try {
                    mChapters=ParseBook.getBookChapters(mBook.getLinkUrl());
                    subscriber.onNext(ParseBook.getIntro(mChapters.get(0).getLinkUrl()));
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
                    public void onNext(String  response) {
                       TypefaceUtils.setText((TextView) v.findViewById(R.id.content),response );
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.down:
                final MySweetAlertDialog dialog=new MySweetAlertDialog(getActivity());
                dialog.loading(getString(R.string.downloading));
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String > subscriber) {
                        try {
                            List<Part> parts =new ArrayList<>();
                            for (Chapter chapter : mChapters) {
                                parts.add(ParseBook.getParts(chapter.getLinkUrl()));
                            }
                            mBook.setParts(parts);
                            mBook.saveThrows();
                            for (Part part : parts) {
                                part.save();
                            }
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
                            public void onNext(String  response) {
                            }

                            @Override
                            public void onCompleted() {
                                dialog.complete();
                                Toast.makeText(getActivity(), getString(R.string.complete), Toast.LENGTH_SHORT).show();
                                dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                                dialog.complete();
                                dismiss();
                            }
                        });
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        if (isDownloading) {
//            Toast.makeText(getActivity(), getString(R.string.wait), Toast.LENGTH_SHORT).show();
//        }else
        super.onDismiss(dialog);
    }
}
