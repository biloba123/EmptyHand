package com.lvqingyang.emptyhand.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lvqingyang.emptyhand.Article.Article;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;
import com.lvqingyang.emptyhand.Word.Word;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CollectActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CollectAdapter mAdapter;
    
    private List<Article> mArticles;
    private List<Word> mWords;
    private List<DataSupport> mDataSupports=new ArrayList<>();
    private DataSupport mSelectData;
    private static final String TAG = "CollectActivity";
    private static final int REQUEST_ARTICLE = 911;
    
    public static void start(Context context) {
        Intent starter = new Intent(context, CollectActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        
        //Back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TypefaceUtils.setText((TextView) findViewById(R.id.title),getString(R.string.my_collect));
        
        mArticles=DataSupport.findAll(Article.class);
        mWords=DataSupport.findAll(Word.class);
        mDataSupports.addAll(mArticles);
        mDataSupports.addAll(mWords);

        mRecyclerView = (RecyclerView) findViewById(R.id.collect_recycler_view);
        if (mDataSupports.size()>0) {
            mAdapter=new CollectAdapter(mDataSupports);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mAdapter);
        }else {
            Toast.makeText(this, getString(R.string.empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ARTICLE:
                if (resultCode==RESULT_OK) {
                    removeItem();
                }
                break;
        }
    }

    public void removeItem(){
        mAdapter.notifyItemRemoved(mDataSupports.indexOf(mSelectData));
        mDataSupports.remove(mSelectData);
    }

    private class CollectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private DataSupport mData;
        private TextView tagtext;
        private TextView datetext;
        private TextView text;
        private TextView aurhortext;
        private int mPos;

        public CollectViewHolder(View v) {
            super(v);
            this.aurhortext = (TextView) v.findViewById(R.id.aurhor_text);
            this.text = (TextView) v.findViewById(R.id.text);
            this.datetext = (TextView) v.findViewById(R.id.date_text);
            this.tagtext = (TextView) v.findViewById(R.id.tag_text);

            View view=v.findViewById(R.id.ll);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void bindData(int pos,DataSupport d){
            mPos=pos;
            mData=d;
            if (mData instanceof Article) {
                Article article=(Article)mData;
                TypefaceUtils.setText(tagtext,getString(R.string.article));
                TypefaceUtils.setText(datetext,article.getDate());
                TypefaceUtils.setText(text,article.getTitle());
                TypefaceUtils.setText(aurhortext,article.getAuthor());
            }else {
                Word word=(Word)mData;
                TypefaceUtils.setText(tagtext,getString(R.string.word));
                TypefaceUtils.setText(datetext,word.getDate());
                TypefaceUtils.setText(text,word.getHitokoto());
                TypefaceUtils.setText(aurhortext,word.getSource());
            }
        }

        @Override
        public void onClick(View v) {
            mSelectData=mData;
            if (mData instanceof Article) {
               startActivityForResult(ArticleActivity.newIntent(CollectActivity.this, (Article) mData),REQUEST_ARTICLE);
            }else {
                WordDialog.newInstance((Word) mData)
                        .show(getSupportFragmentManager(), "WordDialog");
            }
        }

        @Override
        public boolean onLongClick(View v) {
            new SweetAlertDialog(CollectActivity.this,SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText(getString(R.string.delete))
                    .setContentText(getString(R.string.this_coll))
                    .setCancelText(getString(android.R.string.cancel))
                    .setConfirmText(getString(android.R.string.ok))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            mAdapter.notifyItemRemoved(mDataSupports.indexOf(mData));
                            mDataSupports.remove(mData);
                            mData.delete();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();
            return true;
        }
    }

    private class CollectAdapter extends RecyclerView.Adapter<CollectViewHolder>{
        private List<DataSupport> mDataList;

        public CollectAdapter(List<DataSupport> datas){
            mDataList=datas;
        }

        @Override
        public CollectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(CollectActivity.this)
                    .inflate(R.layout.item_collect,parent,false);
            return new CollectViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CollectViewHolder holder, int position) {
            holder.bindData(position,mDataList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

}
