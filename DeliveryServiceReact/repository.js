import { Product } from "./Product.js";
import { DeliveryService } from "./DeliveryService.js";
import { Offer } from "./Offer.js";

export class Repository {
    constructor() {
        this.products = [
            // new Product(1,"Pizza Capricciosa","Description1"),
            // new Product(2,"Pizza Prosciutto e Funghi","Description2"),
            // new Product(3,"Pizza Diavola","Description3"),
            // new Product(4,"Pizza 4 Formaggi","Description4"),
            // new Product(5,"Pizza Marinara","Description5")
        ];
        this.deliveryServices = [
            new DeliveryService("Pizza Hut", "Str. Vaida Voievod 53 - 55"),
            new DeliveryService("Grande Pizza", "Str. București 49"),
            new DeliveryService("Pizza Venezia", "Str. Constantin Brâncuși 101"),
            new DeliveryService("New Croco", "Str. Victor Babeș 12"),
            new DeliveryService("Pronto Pizza", " Calea Turzii 185")
        ];

        this.offers = [
            new Offer(1,this.products[0],this.deliveryServices[0],10),
        ];
        this.ids = 1;
        this.ids_offer = 1;
    }

    add(name,description) {
        AsyncStorage.setItem('@deliveryServices:' + String(this.ids), JSON.stringify(
            {
                id: this.ids,
                name: name,
                description: description
            })).done();
        this.products.push(new Product(this.ids, name,description));
        this.ids += 1;
    }

    refresh(products) {
        //this.refreshing = true;
        this.products = products;
    }

    getProducts() {
        let data = [];
        for (let i = 0; i < this.products.length; ++i) {
            data.push(this.products[i].name);
        }
        return data;
    }
};