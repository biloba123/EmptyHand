package com.lvqingyang.emptyhand.Article;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.lvqingyang.emptyhand.Base.BaseFragment;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.Address;
import com.lvqingyang.emptyhand.Tools.MyOkHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author：LvQingYang
 * Date：2017/2/2
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class ArticleFragment extends BaseFragment {
    //View
    private TextView datetext;
    private TextView titletext;
    private TextView authortext;
    private TextView titlecontent;
    private LikeButton mCollectBtn,mShareBtn;
    //Data
    private Article mArticle;
    //Other
    private static final String TAG = "ArticleFragment";
    private static final String KEY_Article = "Part";

    public static ArticleFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ArticleFragment fragment = new ArticleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mArticle != null) {
            List<Article> articles = DataSupport
                    .where("title=? and author=?",mArticle.getTitle(),mArticle.getAuthor()).find(Article.class);
            if (articles.size()>0) {
                mCollectBtn.setLiked(true);
            }else {
                mCollectBtn.setLiked(false);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_article;
    }
    @Override
    protected void initeView(View view) {
        this.titlecontent = (TextView) view.findViewById(R.id.title_content);
        this.authortext = (TextView) view.findViewById(R.id.author_text);
        this.titletext = (TextView) view.findViewById(R.id.title_text);
        this.datetext = (TextView) view.findViewById(R.id.date_text);
        //收藏
        mCollectBtn=(LikeButton) view.findViewById(R.id.collect_btn);
        mCollectBtn.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton button) {
                        printAll();
                        Log.d(TAG, "liked: ");
                        mArticle=new Article(mArticle);
                        mArticle.save();
                        printAll();
                        Toast.makeText(getActivity(), getString(R.string.collect), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void unLiked(LikeButton button) {
                        printAll();
                        Log.d(TAG, "unLiked: ");
                        DataSupport.deleteAll(Article.class,"title=? and author=?",mArticle.getTitle(),mArticle.getAuthor());
                        printAll();
                        Toast.makeText(getActivity(), getString(R.string.cancel_collect), Toast.LENGTH_SHORT).show();
                    }
                });
        //分享
        mShareBtn=(LikeButton) view.findViewById(R.id.share_btn);
        mShareBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton button) {
                share(mArticle);
            }

            @Override
            public void unLiked(LikeButton button) {
                share(mArticle);
            }
        });
    }

    void printAll(){
        for (Article article : DataSupport.findAll(Article.class)) {
            Log.d(TAG, "printAll: "+article.getTitle());
        }
    }

    @Override
    protected void initeData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String > subscriber) {
                try {
                    String response= MyOkHttp.getInstance().run(Address.articalUrl);
                    subscriber.onNext(response);
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

                        //解析HTML
                        String title,author;
                        StringBuilder content=new StringBuilder();

                        Document doc = Jsoup.parse(response);
                        Element container=doc.select("div#article_show").first();

                        title=container.select("h1").text();
                        author=container.select("p.article_author").text();

                        Elements articleContent=container.select("div.article_text")
                                .first().select("p");
                        for (Element element : articleContent) {
                            content.append("    "+element.text()+"\n\n");
                        }
                        Log.d(TAG, "onNext: \n"+title+"\n"+author+"\n"+content);

                        //Part
                        mArticle=new Article();
                        mArticle.setTitle(title);
                        mArticle.setAuthor(author);
                        mArticle.setContent(content.toString());
                    }

                    @Override
                    public void onCompleted() {
                        showArticle();
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

    private void showArticle(){
        setText(datetext,mArticle.getDate()+"的 文");
        setText(titletext,mArticle.getTitle());
        setText(authortext,mArticle.getAuthor());
        setText(titlecontent,mArticle.getContent());
        List<Article> articles = DataSupport.where("date=?",mArticle.getDate()).find(Article.class);
        if (articles.size()>0) {
            mCollectBtn.setLiked(true);
        }
    }


    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putParcelable(KEY_Article,mArticle);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        mArticle=savedInstanceState.getParcelable(KEY_Article);
        showArticle();
    }

    private void share(Article a){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT,a.getTitle()+"\n"+a.getAuthor()+"\n"+a.getContent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择"));
    }
}
