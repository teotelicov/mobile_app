package cs.ubbcluj.ro.deliveryservice.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Teo on 04.12.2017.
 */
@Dao
public interface DeliveryServiceDao {

    @Query("select * from DeliveryServiceEntity")
    List<DeliveryServiceEntity> getEntries();

    @Query("select * from DeliveryServiceEntity " +
            " where name like :nameArg")
    DeliveryServiceEntity getEntries(String nameArg);

    @Insert
    void insert(DeliveryServiceEntity ds);

    @Delete
    void delete(DeliveryServiceEntity ds);

    @Update
    void update(DeliveryServiceEntity ds);

    @Query("DELETE FROM DeliveryServiceEntity")
    void nukeAll();
}
