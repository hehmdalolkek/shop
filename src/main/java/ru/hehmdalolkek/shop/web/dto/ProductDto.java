package ru.hehmdalolkek.shop.web.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductDto {

    private Integer productId;

    private String title;

    private double price;

    private boolean active;

}
