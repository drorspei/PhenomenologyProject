package com.wordpress.drorspei.phenomenologyproject.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class SavedPhenomenon implements Parcelable {
    public final Phenomenon phenomenon;
    public final int button;
    public final Date date;

    public SavedPhenomenon(Phenomenon phenomenon, int button, Date date) {
        this.phenomenon = phenomenon;
        this.button = button;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(phenomenon, flags);
        dest.writeInt(button);
        dest.writeLong(date.getTime());
    }

    public static final Parcelable.Creator<SavedPhenomenon> CREATOR = new Parcelable.Creator<SavedPhenomenon>() {
        public SavedPhenomenon createFromParcel(Parcel in) {
            Phenomenon phenomenon = in.readParcelable(Phenomenon.class.getClassLoader());
            int button = in.readInt();
            Date date = new Date(in.readLong());

            return new SavedPhenomenon(phenomenon, button, date);
        }

        public SavedPhenomenon[] newArray(int size) {
            return new SavedPhenomenon[size];
        }
    };
}
