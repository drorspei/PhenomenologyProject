package com.wordpress.drorspei.phenomenologyproject.timedistributions;

import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class PhenomenonTimePoissonDistribution implements IPhenomenonTimeDistribution {
    @Override
    public Date nextTime(Phenomenon phenomenon, Date fromWhen) {
        if (phenomenon.endtime > phenomenon.starttime && phenomenon.howmany > 0) {
            double lambda = phenomenon.howmany / (double)(phenomenon.endtime - phenomenon.starttime);
            double totalHours = Math.max(0.1, -Math.log(new Random().nextDouble()) / lambda);
            int days = (int)(totalHours / (phenomenon.endtime - phenomenon.starttime));
            double hoursIn = (totalHours - days * (phenomenon.endtime - phenomenon.starttime));

            Calendar cal = Calendar.getInstance();
            cal.setTime(fromWhen);
//            cal.add(Calendar.MINUTE, new Random().nextInt(10) + 1);
//            return cal.getTime();

            cal.add(Calendar.DAY_OF_MONTH,  days);
            if (cal.get(Calendar.HOUR_OF_DAY) < phenomenon.starttime) {
                cal.set(Calendar.HOUR_OF_DAY, phenomenon.starttime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            } else if (cal.get(Calendar.HOUR_OF_DAY) > phenomenon.endtime) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, phenomenon.starttime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            }

            double hoursToEndOfDay = phenomenon.endtime - (cal.get(Calendar.HOUR_OF_DAY) + (double)(60 * cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND)) / 3600.);

            if (hoursToEndOfDay > hoursIn) {
                cal.add(Calendar.SECOND, (int)(hoursIn * 3600));
            } else {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, phenomenon.starttime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.add(Calendar.SECOND, (int)((hoursIn - hoursToEndOfDay) * 3600));
            }

            return cal.getTime();
        } else {
            return null;
        }
    }
}
