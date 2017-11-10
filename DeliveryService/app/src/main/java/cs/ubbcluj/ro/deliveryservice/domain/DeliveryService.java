package cs.ubbcluj.ro.deliveryservice.domain;

/**
 * Created by Teo on 10.11.2017.
 */

public class DeliveryService {

    private String name;
    private String address;
    private Product product;

    public DeliveryService(String name, String address, Product product) {
        this.name = name;
        this.address = address;
        this.product = product;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return  name + '\n' + address + '\n' + product.getName();
    }
}
