import { Product } from "./Product.js";
import { DeliveryService } from "./DeliveryService.js";

export class Repository {
    constructor() {
        this.products = [
            new Product("Pizza Capricciosa"),
            new Product("Pizza Prosciutto e Funghi"),
            new Product("Pizza Diavola"),
            new Product("Pizza 4 Formaggi"),
            new Product("Pizza Marinara")
        ];
        this.deliveryServices = [
            new DeliveryService("Pizza Hut", "Str. Vaida Voievod 53 - 55", this.products[0]),
            new DeliveryService("Grande Pizza", "Str. București 49", this.products[2]),
            new DeliveryService("Pizza Venezia", "Str. Constantin Brâncuși 101", this.products[4]),
            new DeliveryService("New Croco", "Str. Victor Babeș 12", this.products[3]),
            new DeliveryService("Pronto Pizza", " Calea Turzii 185", this.products[1])
        ];
    }
};