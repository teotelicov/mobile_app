package cs.ubbcluj.ro.deliveryservice.repository;

import java.util.Date;
import java.util.List;

import cs.ubbcluj.ro.deliveryservice.domain.DeliveryService;
import cs.ubbcluj.ro.deliveryservice.domain.Offer;
import cs.ubbcluj.ro.deliveryservice.domain.Product;
import cs.ubbcluj.ro.deliveryservice.exceptions.DeliveryServiceNotFoundException;
import cs.ubbcluj.ro.deliveryservice.exceptions.OfferNotFoundException;
import cs.ubbcluj.ro.deliveryservice.exceptions.ProductNotFoundException;

/**
 * Created by Teo on 10.11.2017.
 */

public interface Repository {

    //delivery service crud
    void addDeliveryService(DeliveryService deliveryService);
    void editDeliveryService(Integer position, DeliveryService deliveryService) throws DeliveryServiceNotFoundException;
    void deleteDeliveryService(Integer position) throws DeliveryServiceNotFoundException;
    DeliveryService getDeliveryService(Integer position) throws DeliveryServiceNotFoundException;
    List<DeliveryService> getDeliveryServices();
    DeliveryService getDeliveryByName(String deliveryName) throws DeliveryServiceNotFoundException;

    // product crud
    void addProduct(Product product);
    void editProduct(Integer position, Product product) throws ProductNotFoundException;
    void deleteProduct(Integer position) throws ProductNotFoundException;
    void addProducts(List<Product> products);
    List<Product> getProducts();
    Product getProductByName(String productName) throws ProductNotFoundException;

    //offer crud
    void addOffer(Offer offer) throws ProductNotFoundException,DeliveryServiceNotFoundException;
    void deleteOffer(Integer position) throws OfferNotFoundException;
    void editOffer(Integer position,Double price) throws OfferNotFoundException;
    List<Offer> getOffers();
    Offer getOfferByProductNameAndDeliveryName(String productName,String deliveryName) throws OfferNotFoundException;

}
