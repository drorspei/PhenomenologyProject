package com.wordpress.drorspei.phenomenologyproject;

import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;

import java.util.Date;

public interface IPhenomenonTimeDistribution {
    Date nextTime(Phenomenon phenomenon, Date fromWhen);
}
