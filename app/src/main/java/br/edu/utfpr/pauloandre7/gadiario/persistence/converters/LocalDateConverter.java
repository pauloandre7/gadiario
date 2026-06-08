package br.edu.utfpr.pauloandre7.gadiario.persistence.converters;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.ZoneId;

public class LocalDateConverter {


    @TypeConverter
    public static Long fromLocalDateToLong(LocalDate date){

        if(date == null) return null;

        // O compilador me obrigou a usar essa validação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // epoch day é o tempo, em milisegundos, de uma data base definida em 1970 até a data
            // na variável
            return date.toEpochDay();
        }
        return null;
    }

    @TypeConverter
    public static LocalDate fromLongToLocalDate(Long epochDay){

        if(epochDay == null) return null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.ofEpochDay(epochDay);
        }

        return null;
    }

}
