package com.lvqingyang.emptyhand.Word;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.lvqingyang.emptyhand.Base.BaseFragment;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.Address;
import com.lvqingyang.emptyhand.Tools.MyOkHttp;
import com.wenchao.cardstack.CardStack;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author：LvQingYang
 * Date：2017/2/4
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class WordFragment extends BaseFragment {
    //View
    private CardStack mCardStack;
    private LikeButton mCollectBtn,mShareBtn;
    //Data
    private List<Word> mWords=new ArrayList<>();
    private WordAdapter mAdapter;
    private static final String TAG = "WordFragment";
    private static final String KEY_WORD_LIST = "WORD_LIST";

    public static WordFragment newInstance() {

        Bundle args = new Bundle();

        WordFragment fragment = new WordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (mWords.size()>0) {
//            if (isColoected(mWords.get(mCardStack.getCurrIndex()))) {
//                mCollectBtn.setLiked(true);
//            }else {
//                mCollectBtn.setLiked(false);
//            }
//        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_word;
    }


    @Override
    protected void initeView(View v) {
        mCardStack = (CardStack) v.findViewById(R.id.container);
        mAdapter=new WordAdapter(getActivity(), R.layout.item_word,mWords);
        mCardStack.setAdapter(mAdapter);
        mCardStack.setListener(new CardStack.CardEventListener() {
            @Override
            public boolean swipeEnd(int i, float v) {
                return (v>300)? true : false;
            }

            @Override
            public boolean swipeStart(int i, float v) {
                return true;
            }

            @Override
            public boolean swipeContinue(int i, float v, float v1) {
                return true;
            }

            @Override
            public void discarded(int i, int i1) {
                Log.d(TAG, "discarded: "+mCardStack.getCurrIndex()+"=="+i);
                if (isColoected(mWords.get(i))) {
                    mCollectBtn.setLiked(true);
                }else {
                    mCollectBtn.setLiked(false);
                }
                int count=mWords.size()-i;
                if (count<4){
                    initeData();
                }
            }

            @Override
            public void topCardTapped() {
                Toast.makeText(getActivity(), mWords.get(mCardStack.getCurrIndex()).getCatname(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //收藏
        mCollectBtn=(LikeButton) v.findViewById(R.id.collect_btn);
        mCollectBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton button) {
                Log.d(TAG, "liked: "+mCardStack.getCurrIndex());
                new Word(mWords.get(mCardStack.getCurrIndex())).setDate().save();
                Toast.makeText(getActivity(), getString(R.string.collect), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton button) {
                Log.d(TAG, "unLiked: "+mCardStack.getCurrIndex());
                DataSupport.deleteAll(Word.class,"hitokoto=?",mWords.get(mCardStack.getCurrIndex()).getHitokoto());
                Toast.makeText(getActivity(), getString(R.string.cancel_collect), Toast.LENGTH_SHORT).show();
            }
        });
        //分享
        mShareBtn=(LikeButton) v.findViewById(R.id.share_btn);
        mShareBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton button) {
                share( mWords.get(mCardStack.getCurrIndex()));
            }

            @Override
            public void unLiked(LikeButton button) {
                share( mWords.get(mCardStack.getCurrIndex()));
            }
        });
    }

    @Override
    protected void initeData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String > subscriber) {
                try {
                    for (int i = 0; i < 10; i++) {
                        String response= MyOkHttp.getInstance().run(Address.wordUrl);
                        subscriber.onNext(response);
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
                        Log.d(TAG, "onNext: "+response);
                        try {
                            JSONObject object=new JSONObject(response);
                            Word w=new Word();
                            w.setHitokoto(object.getString("hitokoto"));
                            w.setCatname(object.getString("catname"));
                            w.setSource(object.getString("source"));
                            mWords.add(w);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        showContent();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e);
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                        cameError();
                    }
                });
    }

    private boolean isColoected(Word w){
        if (DataSupport.where("hitokoto=?",w.getHitokoto()).find(Word.class).size()>0) {
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(KEY_WORD_LIST, (ArrayList<? extends Parcelable>) mWords);
        getArguments().putParcelable("SAVED_STAYE",bundle);
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        Log.d(TAG, "onSaveState: "+mCardStack.getCurrIndex()+"  "+mWords.size());
        for (int i = 0; i < mCardStack.getCurrIndex(); i++) {
            mWords.remove(0);
        }
        outState.putParcelableArrayList(KEY_WORD_LIST, (ArrayList<? extends Parcelable>) mWords);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        Log.d(TAG, "onRestoreState: "+mCardStack.getCurrIndex()+"  "+mWords.size());
        mWords=savedInstanceState.getParcelableArrayList(KEY_WORD_LIST);
        if (mWords.size()>0) {
            if (isColoected(mWords.get(0))) {
                mCollectBtn.setLiked(true);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void share(Word word){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, word.getHitokoto()+"\n"+(word.getSource().isEmpty()?"":"——《"+word.getSource()+"》"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择"));
    }
}
