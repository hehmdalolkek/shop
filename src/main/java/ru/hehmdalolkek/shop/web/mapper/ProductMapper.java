package ru.hehmdalolkek.shop.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.hehmdalolkek.shop.model.Product;
import ru.hehmdalolkek.shop.web.dto.ProductDto;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );

    @Mapping(source = "id", target = "productId")
    ProductDto productToProductDto(Product product);

    @Mapping(source = "productId", target = "id")
    Product productDtoToProduct(ProductDto productDto);


}
