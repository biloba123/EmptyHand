package com.lvqingyang.emptyhand.Wallpaper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author：LvQingYang
 * Date：2017/2/3
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class Wallpaper implements Parcelable {
    private String title,imgUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.imgUrl);
    }

    public Wallpaper() {
    }

    protected Wallpaper(Parcel in) {
        this.title = in.readString();
        this.imgUrl = in.readString();
    }

    public static final Parcelable.Creator<Wallpaper> CREATOR = new Parcelable.Creator<Wallpaper>() {
        @Override
        public Wallpaper createFromParcel(Parcel source) {
            return new Wallpaper(source);
        }

        @Override
        public Wallpaper[] newArray(int size) {
            return new Wallpaper[size];
        }
    };
}
