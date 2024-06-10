package ru.hehmdalolkek.shop.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {

    private int id;

    private LocalDateTime date;

    private double totalCost;

}
