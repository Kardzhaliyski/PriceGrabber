package org.example;

import java.time.LocalDateTime;

public class Item {
    public Integer id;
    public String name;
    public String itemId;
    public String imgLocation;
    public Double price;
    public LocalDateTime priceChanged;
    public int scanId;
    public String description;

    public Item() {
    }

    public Item(String name, String itemId, String imgLocation, Double price, LocalDateTime priceChanged, int scanId, String description) {
        this.name = name;
        this.itemId = itemId;
        this.imgLocation = imgLocation;
        this.price = price;
        this.priceChanged = priceChanged;
        this.scanId = scanId;
        this.description = description;
    }
}
