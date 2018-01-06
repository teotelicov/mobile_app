package cs.ubbcluj.ro.deliveryservice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Teo on 30.11.2017.
 */

public class Offer {

    public int product_id;

    public int delivery_id;

    public double price;

    public String addedAt;

    public Offer(int product_id, int delivery_id, double price, String addedAt) {
        this.product_id = product_id;
        this.delivery_id = delivery_id;
        this.price = price;
        this.addedAt = addedAt;
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

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}
