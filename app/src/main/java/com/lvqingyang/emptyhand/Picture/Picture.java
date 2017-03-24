package com.lvqingyang.emptyhand.Picture;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author：LvQingYang
 * Date：2016/11/27
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class Picture implements Parcelable {
    private String imgUrl;
    private String title;
    private String text;
    private String detailUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgUrl);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.detailUrl);
    }

    public Picture() {
    }

    protected Picture(Parcel in) {
        this.imgUrl = in.readString();
        this.title = in.readString();
        this.text = in.readString();
        this.detailUrl = in.readString();
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel source) {
            return new Picture(source);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
}
