package com.wordpress.drorspei.phenomenologyproject.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Phenomenon implements Parcelable {
    public final String title;
    public final String[] buttons;
    public final String[] continuations;
    public final int starttime;
    public final int endtime;
    public final int howmany;

    public Phenomenon(String title,
                      String[] buttons,
                      String[] continuations,
                      int starttime, int endtime, int howmany
    ) {
        this.title = title;
        this.buttons = buttons;
        this.continuations = continuations;
        this.starttime = starttime;
        this.endtime = endtime;
        this.howmany = howmany;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringArray(buttons);
        dest.writeStringArray(continuations);
        dest.writeInt(starttime);
        dest.writeInt(endtime);
        dest.writeInt(howmany);
    }

    public static final Parcelable.Creator<Phenomenon> CREATOR = new Parcelable.Creator<Phenomenon>() {
        public Phenomenon createFromParcel(Parcel in) {
            String title = in.readString();
            String[] buttons = in.createStringArray();
            String[] continuations = in.createStringArray();
            int starttime = in.readInt();
            int endtime = in.readInt();
            int howmany = in.readInt();

            return new Phenomenon(title, buttons, continuations, starttime, endtime, howmany);
        }

        public Phenomenon[] newArray(int size) {
            return new Phenomenon[size];
        }
    };

}

