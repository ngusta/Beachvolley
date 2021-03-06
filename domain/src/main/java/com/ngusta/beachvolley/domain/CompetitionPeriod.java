package com.ngusta.beachvolley.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CompetitionPeriod implements Serializable {

    public static final CompetitionPeriod[] COMPETITION_PERIODS = {
            new CompetitionPeriod(1, "TP 01", "2016-01-01", "2016-04-03"),
            new CompetitionPeriod(2, "TP 02", "2016-04-04", "2016-05-22"),
            new CompetitionPeriod(3, "TP 03", "2016-05-23", "2016-06-06"),
            new CompetitionPeriod(4, "TP 04", "2016-06-07", "2016-06-12"),
            new CompetitionPeriod(5, "TP 05", "2016-06-13", "2016-06-26"),
            new CompetitionPeriod(6, "TP 06", "2016-06-27", "2016-07-03"),
            new CompetitionPeriod(7, "TP 07", "2016-07-04", "2016-07-10"),
            new CompetitionPeriod(8, "TP 08", "2016-07-11", "2016-07-17"),
            new CompetitionPeriod(9, "TP 09", "2016-07-18", "2016-07-24"),
            new CompetitionPeriod(10, "TP 10", "2016-07-25", "2016-07-31"),
            new CompetitionPeriod(11, "TP 11", "2016-08-01", "2016-08-07"),
            new CompetitionPeriod(12, "TP 12", "2016-08-08", "2016-08-14"),
            new CompetitionPeriod(13, "TP 13", "2016-08-15", "2016-08-21"),
            new CompetitionPeriod(14, "TP 14", "2016-08-22", "2016-09-04"),
            new CompetitionPeriod(15, "TP 15", "2016-09-05", "2016-10-16"),
            new CompetitionPeriod(16, "TP 16", "2016-10-17", "2016-12-31")
    };

    private int periodNumber;

    private String name;

    private Date startDate;

    private Date endDate;

    public CompetitionPeriod(int periodNumber, String name, String startDate, String endDate) {
        this.periodNumber = periodNumber;
        this.name = name;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            this.startDate = formatter.parse(startDate);
            calendar.setTime(formatter.parse(endDate));
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            this.endDate = calendar.getTime();

        } catch (ParseException e) {
        }
    }

    public static CompetitionPeriod findByName(String name) {
        for (CompetitionPeriod competitionPeriod : COMPETITION_PERIODS) {
            if (competitionPeriod.getName().equalsIgnoreCase(name)) {
                return competitionPeriod;
            }
        }
        throw new IllegalArgumentException("Not a valid competition period.");
    }

    public static CompetitionPeriod findPeriodByDate(Date date) {
        for (CompetitionPeriod competitionPeriod : COMPETITION_PERIODS) {
            if (competitionPeriod.startDate.compareTo(date) <= 0
                    && competitionPeriod.endDate.compareTo(date) >= 0) {
                return competitionPeriod;
            }
        }
        throw new IllegalArgumentException(
                "There is no competition period for the given date " + date.toString());
    }

    public static CompetitionPeriod findPeriodByNumber(int periodNumber) {
        return COMPETITION_PERIODS[periodNumber - 1];
    }

    public static boolean qualifiesForSm(CompetitionPeriod period) {
        return period.getPeriodNumber() >= 1 && period.getPeriodNumber() <= 10;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isCurrent() {
        Date now = new Date();
        return startDate.before(now) && endDate.after(now);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return periodNumber == ((CompetitionPeriod) o).periodNumber;
    }

    @Override
    public int hashCode() {
        return periodNumber;
    }

    @Override
    public String toString() {
        return getName();
    }
}
