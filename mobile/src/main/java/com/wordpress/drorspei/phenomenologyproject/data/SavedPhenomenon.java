package com.wordpress.drorspei.phenomenologyproject.data;

import java.util.Date;

public class SavedPhenomenon {
    public final Phenomenon phenomenon;
    public final int button;
    public final Date date;

    public SavedPhenomenon(Phenomenon phenomenon, int button, Date date) {
        this.phenomenon = phenomenon;
        this.button = button;
        this.date = date;
    }
}
