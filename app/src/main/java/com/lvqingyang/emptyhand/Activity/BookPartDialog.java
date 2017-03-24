package com.lvqingyang.emptyhand.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lvqingyang.emptyhand.Book.BookInfo;
import com.lvqingyang.emptyhand.Book.Part;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Author：LvQingYang
 * Date：2017/2/7
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class BookPartDialog extends DialogFragment {
    private static final String ARG_BOOKINFO = "ARG_BOOKINFO";

    private BookInfo mBook;
    private ImageView mBookIv;
    private TextView mNameTv;
    private TextView mAuthorTv;
    private RecyclerView mPartRv;

    public static BookPartDialog newInstance(BookInfo b) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOKINFO,b);
        BookPartDialog fragment = new BookPartDialog();
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
        mBook=getArguments().getParcelable(ARG_BOOKINFO);

        final View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_book_part,null);
        mBookIv = (ImageView) v.findViewById(R.id.book_image_view);
        mNameTv = (TextView) v.findViewById(R.id.name_text_view);
        mAuthorTv = (TextView) v.findViewById(R.id.author_text_view);


        Glide.with(this)
                .load(mBook.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.bookPageBg)
                .into(mBookIv);
        TypefaceUtils.setText(mNameTv, mBook.getName());
        TypefaceUtils.setText(mAuthorTv,mBook.getAuthor());
        TypefaceUtils.setTypeface((TextView) v.findViewById(R.id.tv) );

        mPartRv = (RecyclerView) v.findViewById(R.id.part_recycler);
        mPartRv.setAdapter(new PartAdapter(mBook));
        mPartRv.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mPartRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    private class PartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPartTv;
        private int mPos;

        public PartViewHolder(View itemView) {
            super(itemView);
            mPartTv= (TextView) itemView.findViewById(R.id.part_text);
            mPartTv.setOnClickListener(this);
        }

        private void bindPart(Part p,int pos){
            mPos=pos;
            TypefaceUtils.setText(mPartTv,p.getTitle());
        }

        @Override
        public void onClick(View v) {
            ReadBookActivity.start(getActivity(),  mBook,mPos);
        }
    }

    private class PartAdapter extends RecyclerView.Adapter<PartViewHolder>{

        private List<Part> mParts;

        public PartAdapter(BookInfo b){
            mParts= DataSupport.where("bookinfo_id=?",b.getId()+"").find(Part.class);
            b.setParts(mParts);
        }

        @Override
        public PartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_part,parent,false);
            return new PartViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PartViewHolder holder, int position) {
            holder.bindPart(mParts.get(position),position);
        }

        @Override
        public int getItemCount() {
            return mParts.size();
        }
    }
}
