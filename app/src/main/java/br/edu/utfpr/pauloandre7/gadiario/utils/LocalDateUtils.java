package br.edu.utfpr.pauloandre7.gadiario.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public final class LocalDateUtils {

    private LocalDateUtils(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatLocalDate(LocalDate date){

        if (date == null) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        // O objeto do param vai receber o formatador para trabalhar a LocalDate.
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long toMilliSeconds(LocalDate date){

        if(date == null) return 0;

        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
