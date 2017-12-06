package cs.ubbcluj.ro.deliveryservice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Teo on 30.11.2017.
 */

public class Offer implements Serializable{

    public long id;

    public Product product;

    public DeliveryService deliveryService;

    public double price;

    public Date addedAt;

    public Offer()
    {

    }
    public Offer(long id, Product product, DeliveryService deliveryService, double price, Date addedAt) {
        this.id = id;
        this.product = product;
        this.deliveryService = deliveryService;
        this.price = price;
        this.addedAt = addedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public void setDeliveryService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return product.getName() + " " + deliveryService.getName() + " " + price + " LEI";
    }
}
