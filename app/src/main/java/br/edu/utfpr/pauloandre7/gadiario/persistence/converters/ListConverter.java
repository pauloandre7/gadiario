package br.edu.utfpr.pauloandre7.gadiario.persistence.converters;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class ListConverter {

    @TypeConverter
    public static String fromList(List<String> list){
        if(list == null ) return null;

        // se não for nula, vou unir os elementos com uma virgula e depois dividir eles em cada virgula
        return String.join(",", list);
    }

    @TypeConverter
    public static List<String> toList(String elements){
        if (elements == null) return null;

        // separa a string em um array de elementos que serão transformados em list
        return Arrays.asList(elements.split(","));
    }
}
