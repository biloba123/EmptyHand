package com.lvqingyang.emptyhand.Word;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;

import java.util.List;

/**
 * Author：LvQingYang
 * Date：2016/11/27
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class WordAdapter extends ArrayAdapter<Word> {

    private int mResourceId;
    private static final String TAG = "CardsDataAdapter";

    public WordAdapter(Context context, int resource, List<Word> words) {
        super(context, resource,words);
        mResourceId=resource;
    }

    @Override
    public View getView(int position, final View contentView, ViewGroup parent){
        Word word = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (contentView==null) {
            view= LayoutInflater.from(getContext()).inflate(mResourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.wordText= (TextView) view.findViewById(R.id.word_text);
            viewHolder.sourceText= (TextView) view.findViewById(R.id.source_text);
            view.setTag(viewHolder);
        }else {
            view=contentView;
            viewHolder= (ViewHolder) view.getTag();
        }
        setText(viewHolder.wordText,word.getHitokoto());
        setText(viewHolder.sourceText,word.getSource().isEmpty()?"":"——《"+word.getSource()+"》");
        return view;
    }

    class ViewHolder{
        TextView wordText;
        TextView sourceText;
    }

    private void setText(TextView textView,String text){
        textView.setText(text);
        TypefaceUtils.setTypeface(textView);
    }

}
