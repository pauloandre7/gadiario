package br.edu.utfpr.pauloandre7.gadiario.persistence.converters;

import androidx.room.TypeConverter;

import br.edu.utfpr.pauloandre7.gadiario.models.AnimalSex;
import br.edu.utfpr.pauloandre7.gadiario.models.EventType;
import br.edu.utfpr.pauloandre7.gadiario.models.ReproductiveStatus;

public class EnumConverter {

    private static final AnimalSex[] animalSexes = AnimalSex.values();
    private static final ReproductiveStatus[] reproductiveStatuses = ReproductiveStatus.values();
    private static final EventType[] eventTypes = EventType.values();

    @TypeConverter
    public static int fromAnimalSexToInt(AnimalSex animalSex){
        if(animalSex == null) return -1;

        return animalSex.ordinal();
    }

    @TypeConverter
    public static AnimalSex fromIntToAnimalSex(int ordinal){
        if (ordinal < 0 || ordinal >= animalSexes.length) return null;

        return animalSexes[ordinal];
    }

    @TypeConverter
    public static int fromReproductiveStatusToInt(ReproductiveStatus status){
        if(status == null) return -1;

        return status.ordinal();
    }

    @TypeConverter
    public static ReproductiveStatus fromIntToReproductiveStatus(int ordinal){
        if (ordinal < 0 || ordinal >= reproductiveStatuses.length) return null;

        return reproductiveStatuses[ordinal];
    }

    @TypeConverter
    public static int fromEventTypeToInt(EventType eventType){
        if(eventType == null) return -1;

        return eventType.ordinal();
    }

    @TypeConverter
    public static EventType fromIntToEventType(int ordinal){
        if (ordinal < 0 || ordinal >= eventTypes.length) return null;

        return eventTypes[ordinal];
    }
}
