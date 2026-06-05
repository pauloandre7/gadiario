package br.edu.utfpr.pauloandre7.gadiario.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;

@Dao
public interface BovineDao {

    // Apenas defini o que eu quero que aconteça (Tipagem pelo @) e o que eu quero receber (long)
    @Insert
    long  insert(Bovine bovine);

    @Delete
    int delete(Bovine bovine);

    @Update
    int update(Bovine bovine);

    // Para buscas eu preciso criar minha própria query mesmo e definir a assinatura do métod abaixo
    @Query("SELECT * FROM bovine ORDER BY name ASC")
    List<Bovine> queryAllAscending();

    @Query("SELECT * FROM bovine ORDER BY name DESC")
    List<Bovine> queryAllDownward();

    // Posso definir parametros na QUery com ":[param]. O param do méttodo precisa ter mesmo nome"
    @Query("SELECT * FROM bovine WHERE id=:id")
    Bovine queryById(long id);

    // Vou ter uma lista de bovinos em um pasto, então quero todos que estão em determinado pasto
    @Query("SELECT * FROM bovine WHERE idPasture=:idPasture")
    List<Bovine> queryBovinesByPasture(long idPasture);

    // Para listar todos os bezerros que nasceram de determinada vaca
    @Query("SELECT * FROM bovine WHERE idMother=:idMother")
    List<Bovine> queryBovinesByMother(long idMother);
}
