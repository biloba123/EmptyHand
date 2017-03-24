package com.lvqingyang.emptyhand.Book;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LvQingYang
 * on 2016/9/17.
 */
public class Chapter implements Parcelable {
    private String name,linkUrl;

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.linkUrl);
    }

    public Chapter() {
    }

    protected Chapter(Parcel in) {
        this.name = in.readString();
        this.linkUrl = in.readString();
    }

    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel source) {
            return new Chapter(source);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
}
