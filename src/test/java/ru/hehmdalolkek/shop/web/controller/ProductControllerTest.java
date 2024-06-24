package ru.hehmdalolkek.shop.web.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hehmdalolkek.shop.service.interfaces.ProductService;
import ru.hehmdalolkek.shop.web.dto.ProductDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    @DisplayName("Given active products, when getAllActiveProducts, then returned active products")
    public void givenActiveProducts_whenGetAllActiveProducts_thenReturnActiveProducts() {
        // given
        ProductDto p1 = ProductDto.builder().active(true).build();
        ProductDto p2 = ProductDto.builder().active(true).build();
        List<ProductDto> activeProducts = List.of(p1, p2);
        when(productService.getAllActiveProducts()).thenReturn(activeProducts);

        // when
        ResponseEntity<?> response = this.productController.getAllActiveProducts();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo(activeProducts);
        verify(this.productService).getAllActiveProducts();
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    @DisplayName("Given productId, when getProductById, then returned product")
    public void givenProductId_whenGetProductById_thenReturnProduct() {
        // given
        int productId = 1;
        ProductDto product = ProductDto.builder().productId(productId).build();
        when(productService.getProductById(anyInt())).thenReturn(product);

        // when
        ResponseEntity<?> response = this.productController.getProductById(productId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo(product);
        verify(this.productService).getProductById(anyInt());
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    @DisplayName("Given product, when createProduct, then returned created product")
    public void givenProduct_whenCreateProduct_thenReturnCreatedProduct() {
        // given
        ProductDto productToCreate = ProductDto.builder()
                .title("Title")
                .price(100.99)
                .active(true)
                .build();
        ProductDto createdProduct = ProductDto.builder()
                .productId(1)
                .title("Title")
                .price(100.99)
                .active(true)
                .build();
        when(productService.createProduct(any(ProductDto.class))).thenReturn(createdProduct);

        // when
        ResponseEntity<?> response =
                this.productController.createProduct(productToCreate, UriComponentsBuilder.newInstance());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo(createdProduct);
        verify(this.productService).createProduct(any(ProductDto.class));
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    @DisplayName("Given product, when updateProduct, then returned updated product")
    public void givenProduct_whenUpdateProduct_thenReturnUpdatedProduct() {
        // given
        int productId = 1;
        ProductDto productToUpdate = ProductDto.builder()
                .title("Title")
                .price(100.99)
                .active(true)
                .build();
        ProductDto updatedProduct = ProductDto.builder()
                .productId(productId)
                .title("Title")
                .price(100.99)
                .active(true)
                .build();
        when(productService.updateProduct(anyInt(), any(ProductDto.class)))
                .thenReturn(updatedProduct);

        // when
        ResponseEntity<?> response =
                this.productController.updateProductById(productId, productToUpdate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo(updatedProduct);
        verify(this.productService).updateProduct(anyInt(), any(ProductDto.class));
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    @DisplayName("Given productId and isHard=false, when deleteProductById, then delete product")
    public void givenProductIdAndIsHardIsFalse_whenDeleteProductById_thenDeleteProduct() {
        // given
        int productId = 1;
        boolean isHard = false;

        // when
        ResponseEntity<?> response = this.productController.deleteProductById(productId, isHard);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(this.productService).softDeleteProductById(anyInt());
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    @DisplayName("Given productId and isHard=true, when deleteProductById, then delete product")
    public void givenProductIdAndIsHardIsTrue_whenDeleteProductById_thenDeleteProduct() {
        // given
        int productId = 1;
        boolean isHard = true;

        // when
        ResponseEntity<?> response = this.productController.deleteProductById(productId, isHard);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(this.productService).hardDeleteProductById(anyInt());
        verifyNoMoreInteractions(this.productService);
    }

}