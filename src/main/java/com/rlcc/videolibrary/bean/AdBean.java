package com.rlcc.videolibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AdBean implements Parcelable {
    public long time;
    public String url;
    public String imgUrl;

    protected AdBean(Parcel in) {
        time = in.readLong();
        url = in.readString();
        imgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeString(url);
        dest.writeString(imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdBean> CREATOR = new Creator<AdBean>() {
        @Override
        public AdBean createFromParcel(Parcel in) {
            return new AdBean(in);
        }

        @Override
        public AdBean[] newArray(int size) {
            return new AdBean[size];
        }
    };
}
