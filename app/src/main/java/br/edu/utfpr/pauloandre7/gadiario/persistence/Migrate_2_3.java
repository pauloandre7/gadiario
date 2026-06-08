package br.edu.utfpr.pauloandre7.gadiario.persistence;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrate_2_3 extends Migration {

    public Migrate_2_3(){
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        // --- MIGRAÇÃO DA TABELA BOVINE ---
        db.execSQL("CREATE TABLE IF NOT EXISTS `Bovine_new` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`tag` TEXT NOT NULL, " +
                "`name` TEXT, " +
                // Eu já tinha, então vou fazer quase a mesma coisa das migrações de Enum
                "`birth` INTEGER, " +
                "`animalSex` INTEGER, " +
                "`breed` TEXT, " +
                "`vaccines` TEXT, " +
                "`repStatus` INTEGER, " +
                "`idPasture` INTEGER NOT NULL, " +
                "`idMother` INTEGER NOT NULL)");

        // copia de uma pra outra.
        db.execSQL("INSERT INTO `Bovine_new` (id, tag, name, birth, animalSex, breed, vaccines, repStatus, idPasture, idMother) " +
                "SELECT id, tag, name, NULL, animalSex, breed, vaccines, repStatus, idPasture, idMother FROM Bovine");

        db.execSQL("DROP TABLE `Bovine` ");

        // Nome e index esperados pelo room (reclamou no logcat)
        db.execSQL("ALTER TABLE `Bovine_new` RENAME TO `Bovine` ");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Bovine_tag` ON `Bovine` (`tag`) ");

        // --- MIGRAÇÃO DA TABELA EVENT ---
        db.execSQL("CREATE TABLE IF NOT EXISTS `Event_new` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`idBovine` INTEGER NOT NULL, " +
                "`type` INTEGER, " +
                "`qtyCalves` INTEGER NOT NULL, " +
                "`idPastureOrigin` INTEGER NOT NULL, " +
                "`idPastureDestination` INTEGER NOT NULL, " +
                "`date` INTEGER NOT NULL, " +
                "`observation` TEXT)");

        // como mudou a coluna, inicializa como 0 (representando 1970-01-01) ou um valor padrão,
        // já que na Model o campo é @NonNull.
        db.execSQL("INSERT INTO `Event_new` (id, idBovine, type, qtyCalves, idPastureOrigin, idPastureDestination, date, observation) " +
                "SELECT id, idBovine, type, qtyCalves, idPastureOrigin, idPastureDestination, 0, observation FROM event");

        db.execSQL("DROP TABLE IF EXISTS `event` ");
        db.execSQL("DROP TABLE IF EXISTS `Event` ");
        db.execSQL("ALTER TABLE `Event_new` RENAME TO `Event` ");

        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Event_idBovine` ON `Event` (`idBovine`) ");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Event_date` ON `Event` (`date`) ");
    }
}
