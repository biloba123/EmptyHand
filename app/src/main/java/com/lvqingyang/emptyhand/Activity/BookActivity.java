package com.lvqingyang.emptyhand.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lvqingyang.emptyhand.Book.BookInfo;
import com.lvqingyang.emptyhand.Book.Part;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookActivity extends AppCompatActivity {
    //View
    private RecyclerView mRecyclerView;
    //Data
    private BookAdapter mAdapter;
    private List<BookInfo> mBookInfos=new ArrayList<>();
    private static final String TAG = "BookActivity";
    
    public static void start(Context context) {
        Intent starter = new Intent(context, BookActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        
        mBookInfos= DataSupport.findAll(BookInfo.class);

        TypefaceUtils.setText((TextView) findViewById(R.id.title),getString(R.string.my_book));
        //Back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (mBookInfos.size()>0) {
            mRecyclerView= (RecyclerView) findViewById(R.id.recycler_view);
            mAdapter=new BookAdapter(mBookInfos);
            RecyclerView.LayoutManager manager=new GridLayoutManager(this, 3);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            Toast.makeText(this, getString(R.string.empty), Toast.LENGTH_SHORT).show();
        }

    }

    private class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView bookImgImgView;
        private BookInfo mBookInfo;

        public BookViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            bookImgImgView= (ImageView) itemView.findViewById(R.id.book_image_view);
        }

        public void bindBook(BookInfo bookInfo){
            mBookInfo=bookInfo;
            Glide.with(BookActivity.this)
                    .load(bookInfo.getImgUrl())
                    .placeholder(R.color.bookPageBg)
                    .into(bookImgImgView);

        }

        @Override
        public void onClick(View view) {
            BookPartDialog.newInstance(mBookInfo)
                    .show(getSupportFragmentManager(), "BookPartDialog");
        }

        @Override
        public boolean onLongClick(View v) {
            new SweetAlertDialog(BookActivity.this,SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText(getString(R.string.delete))
                    .setContentText("《"+mBookInfo.getName()+"》?")
                    .setCancelText(getString(android.R.string.cancel))
                    .setConfirmText(getString(android.R.string.ok))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            mAdapter.notifyItemRemoved(mBookInfos.indexOf(mBookInfo));
                            mBookInfos.remove(mBookInfo);
                            DataSupport.deleteAll(Part.class,"bookinfo_id=?",mBookInfo.getId()+"");
                            mBookInfo.delete();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();
            return true;
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookViewHolder>{
        private List<BookInfo> bookInfos;

        public BookAdapter(List<BookInfo> bookInfos) {
            this.bookInfos = bookInfos;
        }

        @Override
        public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(BookActivity.this);
            View view=inflater.inflate(R.layout.item_book1,parent,false);
            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookViewHolder holder, int position) {
            holder.bindBook(bookInfos.get(position));
        }

        @Override
        public int getItemCount() {
            return bookInfos.size();
        }
    }
    
}
