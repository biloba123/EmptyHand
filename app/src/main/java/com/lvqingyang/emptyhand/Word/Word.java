package com.lvqingyang.emptyhand.Word;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author：LvQingYang
 * Date：2017/2/4
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class Word extends DataSupport implements Parcelable {
    private long id;
    private String hitokoto;
    private String source;
    private String catname;
    private String date;

    public Word(Word word){
        this();
        hitokoto=word.getHitokoto();
        source=word.getSource();
        catname=word.getCatname();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getHitokoto() {
        return hitokoto;
    }

    public void setHitokoto(String hitokoto) {
        this.hitokoto = hitokoto;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getDate() {
        return date;
    }

    public Word setDate() {
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("MM月dd号");
        this.date = format.format(date);
        return this;
    }


    public Word() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.hitokoto);
        dest.writeString(this.source);
        dest.writeString(this.catname);
        dest.writeString(this.date);
    }

    protected Word(Parcel in) {
        this.id = in.readLong();
        this.hitokoto = in.readString();
        this.source = in.readString();
        this.catname = in.readString();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
