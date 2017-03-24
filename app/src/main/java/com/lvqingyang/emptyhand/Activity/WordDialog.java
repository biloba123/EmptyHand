package com.lvqingyang.emptyhand.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Word.Word;

import org.litepal.crud.DataSupport;

import static com.lvqingyang.emptyhand.Tools.TypefaceUtils.setText;

/**
 * Author：LvQingYang
 * Date：2017/2/6
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class WordDialog extends DialogFragment {

    private static final String ARG_WORD = "ARG_WORD";
    private Word mWord;
    private boolean mIsLiked=true;
    private TextView wordText,sourceText;

    private LikeButton mCollectBtn,mShareBtn;

    public static WordDialog newInstance(Word w) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_WORD,w);
        WordDialog fragment = new WordDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mWord=getArguments().getParcelable(ARG_WORD);
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_word,null);

        wordText= (TextView) v.findViewById(R.id.word_text);
        sourceText= (TextView) v.findViewById(R.id.source_text);

        setText(wordText,mWord.getHitokoto());
        setText(sourceText,mWord.getSource().isEmpty()?"":"——《"+mWord.getSource()+"》");


        //收藏
        mCollectBtn=(LikeButton) v.findViewById(R.id.collect_btn);
        mCollectBtn.setLiked(true);
        mCollectBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton button) {
                new Word(mWord).setDate().save();
                mIsLiked=true;
                Toast.makeText(getActivity(), getString(R.string.collect), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton button) {
                DataSupport.deleteAll(Word.class,"hitokoto=?",mWord.getHitokoto());
                mIsLiked=false;
                Toast.makeText(getActivity(), getString(R.string.cancel_collect), Toast.LENGTH_SHORT).show();
            }
        });
        //分享
        mShareBtn=(LikeButton) v.findViewById(R.id.share_btn);
        mShareBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton button) {
                share(mWord);
            }

            @Override
            public void unLiked(LikeButton button) {
                share(mWord);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!mIsLiked)
                ((CollectActivity)getActivity()).removeItem();
    }

    private void share(Word word){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, word.getHitokoto()+"\n"+(word.getSource().isEmpty()?"":"——《"+word.getSource()+"》"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择"));
    }
}
