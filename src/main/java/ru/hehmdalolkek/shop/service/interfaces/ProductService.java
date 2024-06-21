package ru.hehmdalolkek.shop.service.interfaces;

import ru.hehmdalolkek.shop.web.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getAllActiveProducts();

    ProductDto getProductById(int productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(int productId, ProductDto productDto);

    void softDeleteProductById(int productId);

    void hardDeleteProductById(int productId);

}
