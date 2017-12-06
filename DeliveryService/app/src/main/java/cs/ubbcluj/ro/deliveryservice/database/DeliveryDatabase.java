package cs.ubbcluj.ro.deliveryservice.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

import cs.ubbcluj.ro.deliveryservice.domain.DeliveryService;

/**
 * Created by Teo on 04.12.2017.
 */

@Database(entities = {ProductEntity.class, DeliveryServiceEntity.class,OfferEntity.class}, version = 2)
public abstract class DeliveryDatabase extends RoomDatabase {

    static final String DATABASE_NAME = "deliveries-db";

    public abstract ProductDao productDao();

    public abstract DeliveryServiceDao deliveryServiceDao();

    public abstract OfferDao offerDao();

    public abstract ProductDeliveryServicesDao productDeliveryServicesDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
}
