package ru.hehmdalolkek.shop.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hehmdalolkek.shop.service.interfaces.ProductService;
import ru.hehmdalolkek.shop.web.dto.ProductDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllActiveProducts() {
        List<ProductDto> products = this.productService.getAllActiveProducts();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(products);
    }

    @GetMapping("/{productId:\\d+}")
    public ResponseEntity<?> getProductById(@PathVariable int productId) {
        ProductDto product = this.productService.getProductById(productId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(product);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto product, UriComponentsBuilder uriBuilder) {
        ProductDto createdProduct = this.productService.createProduct(product);
        return ResponseEntity.created(
                        uriBuilder
                                .path("/api/v1/products/{productId}")
                                .buildAndExpand(createdProduct.getProductId())
                                .toUri()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdProduct);
    }

    @PutMapping("/{productId:\\d+}")
    public ResponseEntity<?> updateProductById(@PathVariable("productId") int productId,
                                               @RequestBody ProductDto product) {
        ProductDto updatedProduct = this.productService.updateProduct(productId, product);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedProduct);
    }

    @DeleteMapping("/{productId:\\d+}")
    public ResponseEntity<?> deleteProductById(@PathVariable("productId") int productId,
                                               @RequestParam(value = "isHard", defaultValue = "false") boolean isHard) {
        if (isHard) {
            this.productService.hardDeleteProductById(productId);
        } else {
            this.productService.softDeleteProductById(productId);
        }
        return ResponseEntity.ok().build();
    }

}
