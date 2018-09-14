package com.wordpress.drorspei.phenomenologyproject.data;

public class Phenomenon {
    public final int id;
    public final String title;
    public final String button1;
    public final String button2;
    public final String button3;
    public final String conn1;
    public final String conn2;
    public final String conn3;
    public final int starttime;
    public final int endtime;
    public final int howmany;

    public Phenomenon(int id, String title,
                      String button1, String button2, String button3,
                      String conn1, String conn2, String conn3,
                      int starttime, int endtime, int howmany
    ) {
        this.id = id;
        this.title = title;
        this.button1 = button1;
        this.button2 = button2;
        this.button3 = button3;
        this.conn1 = conn1;
        this.conn2 = conn2;
        this.conn3 = conn3;
        this.starttime = starttime;
        this.endtime = endtime;
        this.howmany = howmany;
    }
}

