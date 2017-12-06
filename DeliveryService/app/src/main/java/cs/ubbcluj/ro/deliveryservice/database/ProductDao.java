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
public interface ProductDao {

    @Query("select * from ProductEntity")
    List<ProductEntity> getEntries();

    @Query("select * from ProductEntity " +
            " where name like :nameArg")
    ProductEntity getEntries(String nameArg);

    @Query("select * from ProductEntity where id = :productId")
    ProductEntity loadProduct(int productId);

    @Query("select * from ProductEntity where id = :productId")
    ProductEntity loadProductSync(int productId);

    @Insert
    void insert(ProductEntity product);

    @Delete
    void delete(ProductEntity product);

    @Update
    void update(ProductEntity product);

    @Query("DELETE FROM ProductEntity")
    void nukeAll();

}
