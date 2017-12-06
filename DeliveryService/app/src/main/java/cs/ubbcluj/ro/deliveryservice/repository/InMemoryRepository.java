package cs.ubbcluj.ro.deliveryservice.repository;

import java.util.ArrayList;
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

public class InMemoryRepository implements Repository {

    private List<DeliveryService> deliveryServices;
    private List<Product> products;
    private List<Offer> offers;

    public InMemoryRepository() {
        this.deliveryServices = new ArrayList<>();
        this.products = new ArrayList<>();
        this.offers = new ArrayList<>();

        //add products
        this.products.add(new Product(0,"Pizza Capricciosa","Desc 1"));
        this.products.add(new Product(1,"Pizza Prosciutto e Funghi","Desc 2"));
        this.products.add(new Product(2,"Pizza Diavola","Desc 3"));
        this.products.add(new Product(3,"Pizza Americana","Desc 4"));

       //add delivery services
        this.deliveryServices.add(new DeliveryService(0,"Pizza Hut", "Str. Vaida Voievod 53 - 55"));
        this.deliveryServices.add(new DeliveryService(1,"Grande Pizza", "Str. București 49"));
        this.deliveryServices.add(new DeliveryService(2,"Pizza Venezia", "Str. Constantin Brâncuși 101"));
        this.deliveryServices.add(new DeliveryService(3,"New Croco", "Str. Victor Babeș 12"));
        this.deliveryServices.add(new DeliveryService(4,"Pronto Pizza", " Calea Turzii 185"));

        //add offers
        this.offers.add(new Offer(0,this.products.get(0),this.deliveryServices.get(0),15,new Date()));
        this.offers.add(new Offer(1,this.products.get(1),this.deliveryServices.get(0),20,new Date()));

    }

    @Override
    public void addDeliveryService(DeliveryService deliveryService)
    {
        for (DeliveryService ds : this.deliveryServices) {
            if (ds.getName().equals(deliveryService.getName()))
                return;
        }
          this.deliveryServices.add(deliveryService);


    }

    @Override
    public void editDeliveryService(Integer position, DeliveryService deliveryService)throws DeliveryServiceNotFoundException {

        if (position >= this.deliveryServices.size() || position < 0)
        {
            throw new DeliveryServiceNotFoundException("DeliveryService was not found!");
        }

        for (Offer offer : this.offers) {
            if (offer.deliveryService.getName().equals(deliveryService.getName())) {
                int id = this.offers.indexOf(offer);
                this.offers.get(id).setDeliveryService(deliveryService);
            }
        }

        this.deliveryServices.set(position, deliveryService);

    }

    @Override
    public void deleteDeliveryService(Integer position) throws DeliveryServiceNotFoundException {

        if (position >= this.deliveryServices.size() || position < 0)
        {
            throw new DeliveryServiceNotFoundException("DeliveryService was not found!");
        }

        DeliveryService ds = this.deliveryServices.get(position);
        for (Offer offer : this.offers) {
            if (offer.deliveryService.getName().equals(ds.getName())) {
                this.offers.remove(offer);
            }
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
    public DeliveryService getDeliveryByName(String deliveryName) throws DeliveryServiceNotFoundException {
        for (DeliveryService ds : this.deliveryServices) {
            if (ds.getName().equals(deliveryName)) {
                return ds;
            }
        }
        throw new DeliveryServiceNotFoundException("Delivery service " + deliveryName + " does not exist!");
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
    public void editProduct(Integer position, Product product)throws ProductNotFoundException {

        if (position >= this.products.size() || position < 0)
        {
            throw new ProductNotFoundException("Product was not found!");
        }
        for (Offer offer : this.offers) {
            if (offer.product.getName().equals(product.getName())) {
                int id = this.offers.indexOf(offer);
                this.offers.get(id).setProduct(product);
            }
        }
        this.products.set(position, product);

    }

    @Override
    public void deleteProduct(Integer position) throws ProductNotFoundException {

        if (position >= this.products.size() || position < 0)
        {
            throw new ProductNotFoundException("Product was not found!");
        }
        Product p = this.products.get(position);
        for (Offer offer : this.offers) {
            if (offer.product.getName().equals(p.getName())) {
                this.offers.remove(offer);
            }
        }
        this.products.remove(position);

    }

    @Override
    public void addProducts(List<Product> products) {
        for (Product p : products) {
            this.addProduct(p);
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

    @Override
    public void addOffer(Offer offer) throws ProductNotFoundException,DeliveryServiceNotFoundException
    {

        Boolean productExists = Boolean.FALSE;
        for (Product product : this.products) {
            if (product.getName().equals(offer.getProduct().getName())) {
                productExists = Boolean.TRUE;
            }
        }
        if (productExists.equals(Boolean.FALSE)) {
            throw new ProductNotFoundException("Product " + offer.getProduct().getName() + " is not valid");
        }
        Boolean deliveryExists = Boolean.FALSE;
        for (DeliveryService ds : this.deliveryServices) {
            if (ds.getName().equals(offer.getDeliveryService().getName())) {
                deliveryExists = Boolean.TRUE;
            }
        }
        if (deliveryExists.equals(Boolean.FALSE)) {
            throw new DeliveryServiceNotFoundException("Delivery service " + offer.getProduct().getName() + " is not valid");
        }
        this.offers.add(offer);


    }

    @Override
    public void deleteOffer(Integer position) throws OfferNotFoundException {


        if (position >= this.offers.size() || position < 0)
        {
            throw new OfferNotFoundException("Offer was not found!");
        }
        this.offers.remove(position);
    }

    @Override
    public void editOffer(Integer position,Double price) throws OfferNotFoundException {

        if (position >= this.offers.size() || position < 0)
        {
            throw new OfferNotFoundException("Offer was not found!");
        }
        this.offers.get(position).setPrice(price);

    }

    @Override
    public List<Offer> getOffers() {
        return this.offers;
    }

    @Override
    public Offer getOfferByProductNameAndDeliveryName(String productName, String deliveryName) throws OfferNotFoundException {

        for (Offer offer : this.offers) {
            if (offer.getProduct().getName().equals(productName) && offer.getDeliveryService().getName().equals(deliveryName)) {
                return offer;
            }
        }
        throw new OfferNotFoundException("Offer does not exist!");

    }
}

