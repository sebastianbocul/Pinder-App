package com.pinder.app.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class StringDateToAge {
    public int stringDateToAge(String strDate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return stringDateToAgeOreo(strDate);
        } else {
            return stringDateToAgeOld(strDate);
        }
    }

    public int stringDateToAgeOld(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar dob = Calendar.getInstance();
        try {
            dob.setTime(sdf.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar today = Calendar.getInstance();
        int curYear = today.get(Calendar.YEAR);
        int dobYear = dob.get(Calendar.YEAR);
        int age = curYear - dobYear;
        // if dob is month or day is behind today's month or day
        // reduce age by 1
        int curMonth = today.get(Calendar.MONTH);
        int dobMonth = dob.get(Calendar.MONTH);
        if (dobMonth > curMonth) { // this year can't be counted!
            age--;
        } else if (dobMonth == curMonth) { // same month? check for day
            int curDay = today.get(Calendar.DAY_OF_MONTH);
            int dobDay = dob.get(Calendar.DAY_OF_MONTH);
            if (dobDay > curDay) { // this year can't be counted!
                age--;
            }
        }
        return age;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int stringDateToAgeOreo(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate date = LocalDate.parse(strDate, formatter);
        Date c = Calendar.getInstance().getTime();
        LocalDate today = LocalDate.now();
        return calculateAge(date, today);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
}
