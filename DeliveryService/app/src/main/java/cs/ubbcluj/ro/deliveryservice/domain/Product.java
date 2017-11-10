package cs.ubbcluj.ro.deliveryservice.domain;

/**
 * Created by Teo on 10.11.2017.
 */

public class Product {

    private String name;

    public Product(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
