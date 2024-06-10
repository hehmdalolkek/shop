package ru.hehmdalolkek.shop.model;

import lombok.Data;

@Data
public class OrderProduct {

    private int id;

    private int orderId;

    private int productId;

    private int amount;

}
