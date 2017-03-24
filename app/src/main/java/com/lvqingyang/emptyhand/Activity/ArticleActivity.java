package com.lvqingyang.emptyhand.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.lvqingyang.emptyhand.Article.Article;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;

import org.litepal.crud.DataSupport;

/**
 * Author：LvQingYang
 * Date：2017/2/6
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class ArticleActivity extends AppCompatActivity {

    //View
    private TextView datetext;
    private TextView titletext;
    private TextView authortext;
    private TextView titlecontent;
    private LikeButton mCollectBtn,mShareBtn;
    private ImageView mBackIv;
    //Data
    private static final String KEY_ARTICLE = "ARTICLE";
    private Article mArticle;
    private boolean mIsLiked=true;

    public static Intent newIntent(Context context, Article a) {
        Intent starter = new Intent(context, ArticleActivity.class);
        starter.putExtra(KEY_ARTICLE,a);
        return starter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mArticle=getIntent().getParcelableExtra(KEY_ARTICLE);

        mBackIv = (ImageView) findViewById(R.id.back);
        mBackIv.setVisibility(View.VISIBLE);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.titlecontent = (TextView) findViewById(R.id.title_content);
        this.authortext = (TextView) findViewById(R.id.author_text);
        this.titletext = (TextView) findViewById(R.id.title_text);
        this.datetext = (TextView) findViewById(R.id.date_text);
        TypefaceUtils.setText(datetext,mArticle.getDate());
        TypefaceUtils.setText(titletext,mArticle.getTitle());
        TypefaceUtils.setText(authortext,mArticle.getAuthor());
        TypefaceUtils.setText(titlecontent,mArticle.getContent());
        //收藏
        mCollectBtn=(LikeButton) findViewById(R.id.collect_btn);
        mCollectBtn.setLiked(true);
        mCollectBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton button) {
                mIsLiked=true;
                mArticle=new Article(mArticle);
                mArticle.save();
                Toast.makeText(ArticleActivity.this, getString(R.string.collect), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton button) {
                mIsLiked=false;
                DataSupport.delete(Article.class,mArticle.getId());
                Toast.makeText(ArticleActivity.this, getString(R.string.cancel_collect), Toast.LENGTH_SHORT).show();
            }
        });
        //分享
        mShareBtn=(LikeButton) findViewById(R.id.share_btn);
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

    @Override
    public void finish() {
        if (!mIsLiked) {
            setResult(RESULT_OK);
        }
        super.finish();
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
