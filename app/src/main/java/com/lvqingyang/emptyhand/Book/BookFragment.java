package com.lvqingyang.emptyhand.Book;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lvqingyang.emptyhand.Base.BaseFragment;
import com.lvqingyang.emptyhand.R;

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

public class BookFragment extends BaseFragment {
    //View
    private RecyclerView mRecyclerView;
    //Data
    private BookAdapter mAdapter;
    private List<BookInfo> mBookInfos=new ArrayList<>();
    private static final String TAG = "BookFragment";
    private static final String KEY_BOOK_LIST = "BOOK_LIST";
    private static final String DIALOG_DETAIL = "DIALOG_DETAIL";


    public static BookFragment newInstance() {

        Bundle args = new Bundle();

        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    protected void initeView(View v) {
        mRecyclerView= (RecyclerView) v.findViewById(R.id.recycler_view);
        mAdapter=new BookAdapter(mBookInfos);
        RecyclerView.LayoutManager manager=new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                        >= recyclerView.computeVerticalScrollRange()){
                    Snackbar.make(mRecyclerView, R.string.load_more, Snackbar.LENGTH_SHORT).show();
                    initeData();
                }
            }
        });



    }

    @Override
    protected void initeData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String > subscriber) {
                try {
                    if (mBookInfos.size()==0) {
                        ParseBook.setPage();
                    }
                    List<BookInfo> bookInfos=ParseBook.getOnePageBooks();
                    if (bookInfos.size()>0) {
                        mBookInfos.addAll(bookInfos);
                    }else {
                        Snackbar.make(mRecyclerView, R.string.no_more, Snackbar.LENGTH_SHORT).show();
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

    private class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView bookImgImgView;
        private TextView nameTextView;
        private TextView authorTextView;
        private BookInfo mBookInfo;

        public BookViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            bookImgImgView= (ImageView) itemView.findViewById(R.id.book_image_view);
            nameTextView= (TextView) itemView.findViewById(R.id.name_text_view);
            authorTextView= (TextView) itemView.findViewById(R.id.author_text_view);
        }

        public void bindBook(BookInfo bookInfo){
            mBookInfo=bookInfo;
            Glide.with(getActivity())
                    .load(bookInfo.getImgUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.bookPageBg)
                    .into(bookImgImgView);
            setText(nameTextView,mBookInfo.getName());
            setText(authorTextView,mBookInfo.getAuthor());
        }

        @Override
        public void onClick(View view) {
            BookDteailDialog.newInstance(mBookInfo)
                    .show(getFragmentManager(),DIALOG_DETAIL );
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookViewHolder>{
        private List<BookInfo> bookInfos;

        public BookAdapter(List<BookInfo> bookInfos) {
            this.bookInfos = bookInfos;
        }

        @Override
        public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View view=inflater.inflate(R.layout.item_book,parent,false);
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

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putParcelableArrayList(KEY_BOOK_LIST, (ArrayList<? extends Parcelable>) mBookInfos);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        mBookInfos=savedInstanceState.getParcelableArrayList(KEY_BOOK_LIST);
        mAdapter.notifyDataSetChanged();
    }
}
