package com.lvqingyang.emptyhand.Book;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by LvQingYang
 * on 2016/9/17.
 */
public class BookInfo extends DataSupport implements Parcelable {
    private long id;
    private String name,author,imgUrl,linkUrl;
    private List<Part> parts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public BookInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.author);
        dest.writeString(this.imgUrl);
        dest.writeString(this.linkUrl);
        dest.writeTypedList(this.parts);
    }

    protected BookInfo(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.author = in.readString();
        this.imgUrl = in.readString();
        this.linkUrl = in.readString();
        this.parts = in.createTypedArrayList(Part.CREATOR);
    }

    public static final Parcelable.Creator<BookInfo> CREATOR = new Parcelable.Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel source) {
            return new BookInfo(source);
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };
}
