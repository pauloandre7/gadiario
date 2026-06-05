package br.edu.utfpr.pauloandre7.gadiario.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.models.Event;

@Dao
public interface EventDao {

    @Insert
    long insert(Event event);

    @Delete
    int delete(Event event);

    @Update
    int update(Event event);

    @Query("SELECT * FROM event ORDER BY date ASC")
    List<Event> queryAllAscending();

    @Query("SELECT * FROM event ORDER BY date DESC")
    List<Event> queryAllDownward();

    @Query("SELECT * FROM event WHERE id=:id")
    Event queryById(long id);

    @Query("SELECT * FROM event WHERE idBovine=:idBovine ORDER BY date DESC")
    List<Event> queryEventsByBovine(long idBovine);
}
