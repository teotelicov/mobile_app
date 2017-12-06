package cs.ubbcluj.ro.deliveryservice.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Teo on 05.12.2017.
 */
@Dao
public interface OfferDao {

    @Query("select * from OfferEntity")
    List<OfferEntity> getEntries();

    @Query("select * from OfferEntity " +
            " where product_id  = :productIdArg")
    List<OfferEntity> getEntriesByProduct( int productIdArg);

    @Query("select * from OfferEntity " +
            " where delivery_id = :deliveryIdArg")
    List<OfferEntity> getEntriesByDeliveryService( int deliveryIdArg);

    @Query("select * from ProductEntity " +
            " where id like :productIdArg")
    ProductEntity getProduct( int productIdArg);

    @Query("select * from DeliveryServiceEntity " +
            " where id like :deliveryIdArg")
    DeliveryServiceEntity getDeliveryService(int deliveryIdArg);

    @Insert
    void insert(OfferEntity offer);

    @Delete
    void delete(OfferEntity offer);

    @Update
    void update(OfferEntity offer);

    @Query("DELETE FROM OfferEntity")
    void nukeAll();
}
