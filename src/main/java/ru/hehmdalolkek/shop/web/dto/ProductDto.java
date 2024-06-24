package ru.hehmdalolkek.shop.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductDto {

    private Integer productId;

    @NotNull(message = "Title must not be null")
    @Size(min = 2, max = 255, message = "The size must be greater than 1 and less than 256 characters")
    private String title;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than 0.99")
    private double price;

    private boolean active;

}
