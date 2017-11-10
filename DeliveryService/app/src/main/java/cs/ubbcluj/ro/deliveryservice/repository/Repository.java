package cs.ubbcluj.ro.deliveryservice.repository;

import java.util.List;

import cs.ubbcluj.ro.deliveryservice.domain.DeliveryService;
import cs.ubbcluj.ro.deliveryservice.domain.Product;
import cs.ubbcluj.ro.deliveryservice.exceptions.DeliveryServiceNotFoundException;
import cs.ubbcluj.ro.deliveryservice.exceptions.ProductNotFoundException;

/**
 * Created by Teo on 10.11.2017.
 */

public interface Repository {

    void addDeliveryService(DeliveryService deliveryService) throws ProductNotFoundException;
    void editDeliveryService(Integer position, DeliveryService deliveryService) throws ProductNotFoundException;
    void deleteDeliveryService(Integer position) throws DeliveryServiceNotFoundException;
    DeliveryService getDeliveryService(Integer position) throws DeliveryServiceNotFoundException;
    List<DeliveryService> getDeliveryServices();
    void addProduct(Product product);
    void addProducts(String[] productNames);
    List<Product> getProducts();

    Product getProductByName(String productName) throws ProductNotFoundException;
}
