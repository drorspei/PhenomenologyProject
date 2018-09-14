package com.wordpress.drorspei.phenomenologyproject.data;

import java.util.Date;

public class ScheduleItem {
    public final Phenomenon phenomenon;
    public final Date date;

    public ScheduleItem(Phenomenon phenomenon, Date date) {
        this.phenomenon = phenomenon;
        this.date = date;
    }
}
