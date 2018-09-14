package com.wordpress.drorspei.phenomenologyproject.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ScheduleItem implements Parcelable {
    public final Phenomenon phenomenon;
    public final Date date;

    public ScheduleItem(Phenomenon phenomenon, Date date) {
        this.phenomenon = phenomenon;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(phenomenon, flags);
        dest.writeLong(date.getTime());
    }

    public static final Parcelable.Creator<ScheduleItem> CREATOR = new Parcelable.Creator<ScheduleItem>() {
        public ScheduleItem createFromParcel(Parcel in) {
            Phenomenon phenomenon = in.readParcelable(Phenomenon.class.getClassLoader());
            Date date = new Date(in.readLong());

            return new ScheduleItem(phenomenon, date);
        }

        public ScheduleItem[] newArray(int size) {
            return new ScheduleItem[size];
        }
    };
}
