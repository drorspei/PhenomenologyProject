package com.wordpress.drorspei.phenomenologyproject.timedistributions;

import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;

import java.util.Date;

public interface IPhenomenonTimeDistribution {
    /**
     * Get a random time for when to show the phenomenon notifiaction next.
     *
     * @param phenomenon Phenomenon instance.
     * @param fromWhen Date from which to go from. This is to keep the function pure.
     * @return Date when to show next notification.
     */
    Date nextTime(Phenomenon phenomenon, Date fromWhen);
}
