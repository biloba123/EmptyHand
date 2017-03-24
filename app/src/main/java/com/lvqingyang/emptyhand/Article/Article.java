package com.lvqingyang.emptyhand.Article;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author：LvQingYang
 * Date：2017/2/2
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class Article extends DataSupport implements Parcelable {
    private String date,title,author,content;
    private long id;

    public Article(Article article){
        this();
        title=article.getTitle();
        content=article.getContent();
        author=article.getAuthor();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Article(){
        date=getDateStr();
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private  static String getDateStr(){
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("MM月dd号");
        return format.format(date);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeLong(this.id);
    }

    protected Article(Parcel in) {
        this.date = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
