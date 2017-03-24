package com.lvqingyang.emptyhand.Book;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by LvQingYang
 * on 2016/9/18.
 */
public class Part extends DataSupport implements Parcelable {
    private long id;
    private String title,content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
    }

    public Part() {
    }

    protected Part(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Part> CREATOR = new Parcelable.Creator<Part>() {
        @Override
        public Part createFromParcel(Parcel source) {
            return new Part(source);
        }

        @Override
        public Part[] newArray(int size) {
            return new Part[size];
        }
    };
}
