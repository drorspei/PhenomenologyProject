package com.wordpress.drorspei.phenomenologyproject.data;

import java.util.Date;

public interface IScheduleDb {
    /**
     * Gets the next {@link ScheduleItem} to be shown.
     *
     * @return instance or null if schedule is empty.
     */
    ScheduleItem getNext();

    /**
     * Add a phenomenon notification to schedule at specificied date.
     *
     * @param phenomenon Instance to add.
     * @param date When to show notification.
     */
    void add(Phenomenon phenomenon, Date date);

    /**
     * Remove all scheduled notifications of phenomenon.
     *
     * @param phenomenon Instance to remove.
     */
    void remove(Phenomenon phenomenon);
}
