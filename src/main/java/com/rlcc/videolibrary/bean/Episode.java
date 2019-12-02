package com.rlcc.videolibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;
public class Episode implements Parcelable {
    public String id;
    public String episode;
    public String url;
    public String description;
    public boolean isSelect;
    public double duration;
    public String title;
    public String subtitle;
    public String link;
    public String pid;

    public Episode() {
    }

    protected Episode(Parcel in) {
        id = in.readString();
        episode = in.readString();
        url = in.readString();
        description = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        Episode e = (Episode) obj;
        return e.id.equals(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(episode);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }
}