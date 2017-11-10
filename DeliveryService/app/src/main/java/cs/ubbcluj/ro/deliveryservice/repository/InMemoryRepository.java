package cs.ubbcluj.ro.deliveryservice.repository;

import java.util.ArrayList;
import java.util.List;

import cs.ubbcluj.ro.deliveryservice.domain.DeliveryService;
import cs.ubbcluj.ro.deliveryservice.domain.Product;
import cs.ubbcluj.ro.deliveryservice.exceptions.DeliveryServiceNotFoundException;
import cs.ubbcluj.ro.deliveryservice.exceptions.ProductNotFoundException;

/**
 * Created by Teo on 10.11.2017.
 */

public class InMemoryRepository implements Repository {

    private List<DeliveryService> deliveryServices;
    private List<Product> products;

    public InMemoryRepository() {
        this.deliveryServices = new ArrayList<>();
        this.products = new ArrayList<>();
        this.addProducts(new String[]{
                "Pizza Capricciosa",
                "Pizza Prosciutto e Funghi",
                "Pizza Diavola",
                "Pizza Americana",
                "Pizza 4 Formaggi",
                "Pizza Marinara"
        });

        try {
            this.deliveryServices.add(new DeliveryService("Pizza Hut", "Str. Vaida Voievod 53 - 55", this.getProductByName("Pizza Capricciosa")));
            this.deliveryServices.add(new DeliveryService("Grande Pizza", "Str. București 49", this.getProductByName("Pizza Diavola")));
            this.deliveryServices.add(new DeliveryService("Pizza Venezia", "Str. Constantin Brâncuși 101", this.getProductByName("Pizza Marinara")));
            this.deliveryServices.add(new DeliveryService("New Croco", "Str. Victor Babeș 12", this.getProductByName("Pizza 4 Formaggi")));
            this.deliveryServices.add(new DeliveryService("Pronto Pizza", " Calea Turzii 185", this.getProductByName("Pizza Prosciutto e Funghi")));
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDeliveryService(DeliveryService deliveryService) throws ProductNotFoundException {

        Boolean productExists = Boolean.FALSE;
        for (Product product : this.products) {
            if (product.getName().equals(deliveryService.getProduct().getName())) {
                productExists = Boolean.TRUE;
            }
        }
        if (productExists.equals(Boolean.FALSE)) {
            throw new ProductNotFoundException("Product" + deliveryService.getProduct().getName() + " is not valid");
        }
        this.deliveryServices.add(deliveryService);

    }

    @Override
    public void editDeliveryService(Integer position, DeliveryService deliveryService) throws ProductNotFoundException {

        Boolean productExists = Boolean.FALSE;
        for (Product product : this.products) {
            if (product.getName().equals(deliveryService.getProduct().getName())) {
                productExists = Boolean.TRUE;
            }
        }
        if (productExists.equals(Boolean.FALSE)) {
            throw new ProductNotFoundException("Product" + deliveryService.getProduct().getName() + " is not valid");
        }
        this.deliveryServices.set(position, deliveryService);

    }

    @Override
    public void deleteDeliveryService(Integer position) throws DeliveryServiceNotFoundException {

        if (position >= this.deliveryServices.size() || position < 0)
        {
            throw new DeliveryServiceNotFoundException("DeliveryService was not found!");
        }
        this.deliveryServices.remove(position);

    }

    @Override
    public DeliveryService getDeliveryService(Integer position) throws DeliveryServiceNotFoundException {
        if (position >= this.deliveryServices.size() || position < 0)
        {
            throw new DeliveryServiceNotFoundException("DeliveryService was not found!");
        }
        return this.deliveryServices.get(position);
    }

    @Override
    public List<DeliveryService> getDeliveryServices() {
        return this.deliveryServices;
    }

    @Override
    public void addProduct(Product new_product) {
        for (Product product : this.products) {
            if (product.getName().equals(new_product.getName()))
                return;
        }
        this.products.add(new_product);

    }

    @Override
    public void addProducts(String[] productNames) {
        for (String productName : productNames) {
            this.addProduct(new Product(productName));
        }
    }

    @Override
    public List<Product> getProducts() {
        return this.products;
    }

    @Override
    public Product getProductByName(String productName) throws ProductNotFoundException {
        for (Product product : this.products) {
            if (product.getName().equals(productName)) {
                return product;
            }
        }
        throw new ProductNotFoundException("Product " + productName + " does not exist!");
    }
}

