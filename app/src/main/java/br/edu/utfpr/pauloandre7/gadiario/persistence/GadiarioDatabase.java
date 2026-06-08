package br.edu.utfpr.pauloandre7.gadiario.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.persistence.converters.EnumConverter;
import br.edu.utfpr.pauloandre7.gadiario.persistence.converters.ListConverter;
import br.edu.utfpr.pauloandre7.gadiario.persistence.converters.LocalDateConverter;
import br.edu.utfpr.pauloandre7.gadiario.persistence.dao.BovineDao;
import br.edu.utfpr.pauloandre7.gadiario.persistence.dao.EventDao;
import br.edu.utfpr.pauloandre7.gadiario.persistence.dao.PastureDao;

/*
* O room é intermediário, o GadiarioDatabase é o que vai gerenciar o database como um toddo.
* Ela precisa ser abstrata para o Room conseguir manipular ela, além de ser necessário declarar todas
* as entidades.
* O version ajuda a controlar a versão do banco, facilitando a rastreabilidade das alterações
* */
@Database(entities = {Bovine.class, Pasture.class, Event.class}, version = 3)
@TypeConverters({ListConverter.class, EnumConverter.class, LocalDateConverter.class})
public abstract class GadiarioDatabase extends RoomDatabase {

    public abstract BovineDao getBovinesDao();
    public abstract PastureDao getPastureDao();
    public abstract EventDao getEventDao();

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
                    /*
                    // O builder do Room que cria a instância, recebendo o contexto, Bovines e o título.
                    INSTANCE = Room.databaseBuilder(context,
                            GadiarioDatabase.class,
                            "gadiario.db").allowMainThreadQueries().build();
                    // em apps profissionais, o allowMain... não é recomendado, pois a ideia é
                    // acessar o database em threads separadas
                     */

                    // com o padrão builder é possível construir a instância em etapas.
                    Builder builder = Room.databaseBuilder(context,
                            GadiarioDatabase.class,
                            "gadiario.db"
                    );

                    builder.allowMainThreadQueries();

                    // o fallBack vai destruir tudo a cada nova versão
                    // Facilita para o dev, mas em prod faz o usuário perder todos os dados.
                    // builder.fallbackToDestructiveMigration();

                    builder.addMigrations(new Migrate_1_2());
                    builder.addMigrations(new Migrate_2_3());

                    INSTANCE = (GadiarioDatabase) builder.build();
                }
            }
        }
        return INSTANCE;
    }

}
