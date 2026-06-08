package br.edu.utfpr.pauloandre7.gadiario.persistence;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrate_1_2 extends Migration {

    public Migrate_1_2(){
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS `Bovine_provisional` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`tag` TEXT NOT NULL, " +
                "`name` TEXT, " +
                "`date` TEXT, " +
                "`animalSex` INTEGER, " +
                "`breed` TEXT, " +
                "`vaccines` TEXT, " +
                "`repStatus` INTEGER, " +
                "`idPasture` INTEGER NOT NULL, " +
                "`idMother` INTEGER NOT NULL)"
        );

        db.execSQL("INSERT INTO `Bovine_provisional` " +
                "(id, tag, name, date, animalSex, breed, vaccines, repStatus, idPasture, idMother) " +
                "SELECT id, tag, name, date, " +
                // ao invés de colocar o param animalSex após o date, coloca o Case para modificar
                // o dado retornado
                "(CASE animalSex " +
                "  WHEN 'MALE' THEN 0 " +
                "  WHEN 'FEMALE' THEN 1 " +
                "  ELSE -1 END), " +

                "breed, vaccines, " +

                "(CASE repStatus " +
                "  WHEN 'PREGNANT' THEN 0 " +
                "  WHEN 'LACTATING' THEN 1 " +
                "  WHEN 'DRY' THEN 2 " +
                "  WHEN 'READY' THEN 3 " +
                "  ELSE -1 END), " +

                "idPasture, idMother " +

                "FROM bovine" // Nome da tabela original
        );

        // Remove a tabela antiga
        db.execSQL("DROP TABLE `bovine` ");
        // Renomear a provisória para o nome oficial
        db.execSQL("ALTER TABLE `Bovine_provisional` RENAME TO `bovine` ");
        // Recriar o índice da coluna 'tag' que existia na versão 1
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bovine_tag` ON `bovine` (`tag`) ");


        // --- MIGRAÇÃO DA TABELA EVENT ---

        db.execSQL("CREATE TABLE IF NOT EXISTS `Event_provisional` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`idBovine` INTEGER NOT NULL, " +
                "`type` INTEGER, " +
                "`qtyCalves` INTEGER NOT NULL, " +
                "`idPastureOrigin` INTEGER NOT NULL, " +
                "`idPastureDestination` INTEGER NOT NULL, " +
                "`date` TEXT NOT NULL, " +
                "`observation` TEXT)"
        );

        db.execSQL("INSERT INTO `Event_provisional` " +
                "(id, idBovine, type, qtyCalves, idPastureOrigin, idPastureDestination, date, observation) " +
                "SELECT id, idBovine, " +
                "(CASE type " +
                "  WHEN 'VACCINATION' THEN 0 WHEN 'VACINACAO' THEN 0 " +
                "  WHEN 'INSEMINATION' THEN 1 WHEN 'INSEMINACAO' THEN 1 " +
                "  WHEN 'CALVING' THEN 2 WHEN 'PARTO' THEN 2 " +
                "  WHEN 'MOVEMENT' THEN 3 WHEN 'MOVIMENTACAO' THEN 3 " +
                "  WHEN 'SLAUGHTER' THEN 4 WHEN 'ABATE' THEN 4 " +
                "  ELSE -1 END), " +
                "qtyCalves, idPastureOrigin, idPastureDestination, date, observation " +
                "FROM event"
        );

        db.execSQL("DROP TABLE `event` ");
        db.execSQL("ALTER TABLE `Event_provisional` RENAME TO `event` ");

        db.execSQL("CREATE INDEX IF NOT EXISTS `index_event_idBovine` ON `event` (`idBovine`) ");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_event_date` ON `event` (`date`) ");
    }
}