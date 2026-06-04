package br.edu.utfpr.pauloandre7.gadiario.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.persistence.converters.Converters;
import br.edu.utfpr.pauloandre7.gadiario.persistence.dao.BovineDao;

/*
* O room é intermediário, o GadiarioDatabase é o que vai gerenciar o database como um toddo.
* Ela precisa ser abstrata para o Room conseguir manipular ela, além de ser necessário declarar todas
* as entidades.
* O version ajuda a controlar a versão do banco, facilitando a rastreabilidade das alterações
* */
@Database(entities = {Bovine.class, Pasture.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GadiarioDatabase extends RoomDatabase {

    public abstract BovineDao getBovinesDao();

    // padrão singleton para forçar apenas uma instância dessa classe
    private static GadiarioDatabase INSTANCE;

    public static GadiarioDatabase getInstance(final Context context){

        if(INSTANCE == null){

            // Pode haver de várias threads querer acessar o database, então esse trecho abaixo
            // restringe o acesso a apenas uma por vez
            synchronized (GadiarioDatabase.class){
                // verifica se ainda é null, porque aqui é a parte restrita mais lenta.
                // pode ocorrer de várias threads passarem pelo primeiro if, então precsia verificar
                if(INSTANCE == null){
                    // O builder do Room que cria a instância, recebendo o contexto, Bovines e o título.
                    INSTANCE = Room.databaseBuilder(context,
                            GadiarioDatabase.class,
                            "gadiario.db").allowMainThreadQueries().build();
                    // em apps profissionais, o allowMain... não é recomendado
                }
            }
        }
        return INSTANCE;
    }

}
