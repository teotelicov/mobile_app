package cs.ubbcluj.ro.deliveryservice.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

/**
 * Created by Teo on 05.12.2017.
 */
@Dao
public interface ProductDeliveryServicesDao {
    @Query("select * from DeliveryServiceEntity")
    @Transaction
    List<DeliveryServiceEntity> productWithTheirDeliveryServices();
    @Insert
    void add(OfferEntity offer);
}