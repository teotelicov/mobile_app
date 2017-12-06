package cs.ubbcluj.ro.deliveryservice.domain;

import android.util.EventLogTags;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Teo on 10.11.2017.
 */

public class Product implements Serializable{

    private long id;

    private String name;

    private String description;

    public Product(){}

    public Product(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " ";
    }

}
