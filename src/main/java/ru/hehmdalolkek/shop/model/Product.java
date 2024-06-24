package ru.hehmdalolkek.shop.model;

import lombok.Data;

@Data
public class Product {

    private Integer id;

    private String title;

    private Double price;

    private Boolean active;

}
