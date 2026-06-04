package br.edu.utfpr.pauloandre7.gadiario.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;

@Dao
public interface PastureDao {

    @Insert
    long insert(Pasture pasture);

    @Delete
    int delete(Pasture pasture);

    @Update
    int update(Pasture pasture);

    @Query("SELECT * FROM pasture WHERE id=:id")
    List<Pasture> queryAllAscending(long id);

    @Query("SELECT * FROM pasture ORDER BY name ASC")
    List<Pasture> queryAllAscending();

    @Query("SELECT * FROM pasture ORDER BY name DESC")
    List<Pasture> queryAllDownward();
}
