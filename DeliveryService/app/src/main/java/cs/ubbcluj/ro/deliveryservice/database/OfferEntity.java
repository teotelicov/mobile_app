package cs.ubbcluj.ro.deliveryservice.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import static android.arch.persistence.room.ForeignKey.CASCADE;

import java.util.Date;
import cs.ubbcluj.ro.deliveryservice.database.converter.DateConverter;

/**
 * Created by Teo on 04.12.2017.
 */
@TypeConverters(DateConverter.class)
@Entity(
        indices = {@Index("product_id"),@Index("delivery_id")},
        foreignKeys ={
        @ForeignKey(entity = ProductEntity.class,
                parentColumns = "id",
                childColumns = "product_id",
                onDelete = CASCADE
        ), @ForeignKey(entity = DeliveryServiceEntity.class,
                parentColumns = "id",
                childColumns = "delivery_id",
                onDelete = CASCADE)})
public class OfferEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "product_id")
    private int product_id;
    @ColumnInfo(name = "delivery_id")
    private int delivery_id;
    private double price;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(int delivery_id) {
        this.delivery_id = delivery_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "OfferEntity{" +
                "id=" + id +
                ", product_id=" + product_id +
                ", delivery_id=" + delivery_id +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
