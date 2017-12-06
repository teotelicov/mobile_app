package cs.ubbcluj.ro.deliveryservice.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Teo on 10.11.2017.
 */

public class DeliveryService implements Serializable{

    private long id;
    private String name;
    private String address;


    public DeliveryService(){

    }

    public DeliveryService(long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name + " ";
    }
}
