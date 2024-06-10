package ru.hehmdalolkek.shop.model;

import lombok.Data;

@Data
public class Product {

    private int id;

    private String title;

    private double price;

    private boolean active;

}
