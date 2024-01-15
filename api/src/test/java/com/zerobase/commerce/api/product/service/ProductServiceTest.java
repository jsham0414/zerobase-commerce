package com.zerobase.commerce.api.product.service;

import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.ProductDto;
import com.zerobase.commerce.api.product.dto.SearchProductByName;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.review.repository.ReviewRepository;
import com.zerobase.commerce.database.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static final LocalDateTime now = LocalDateTime.now();
    @InjectMocks
    private ProductService productService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private WishlistRepository wishlistRepository;
    private Product product;

    @BeforeEach
    public void setUp() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("코카콜라 뚱캔")
                .sellerId("test")
                .price(1000L)
                .discount(0.0)
                .status(ProductStatus.PUBLIC)
                .updatedAt(now)
                .publishedAt(now)
                .star(4.5)
                .build();
    }

    @Test
    void addProductSuccess() {
        AddProduct addProduct = AddProduct.builder()
                .price(1000L)
                .name("코카콜라 뚱캔")
                .build();

        given(productRepository.save(any())).willReturn(product);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        productService.addProduct("test", addProduct);

        verify(productRepository, times(1)).save(captor.capture());

        assertAll(
                () -> assertEquals(captor.getValue().getSellerId(), "test"),
                () -> assertEquals(captor.getValue().getName(), addProduct.getName()),
                () -> assertEquals(captor.getValue().getPrice(), addProduct.getPrice())
        );
    }

    @Test
    void getProductSuccess() {
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        ProductDto productDto = productService.getProduct(product.getId());

        verify(productRepository, times(1)).findById(any());

        assertEquals(productDto.getId(), product.getId());
    }

    @Test
    void updateProductSuccess() {
        UpdateProduct updateProduct = UpdateProduct.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name("코카콜라 작은 캔")
                .price(800L)
                .discount(10.0)
                .status(ProductStatus.LINK_ONLY.name())
                .build();

        given(productRepository.findById(any())).willReturn(Optional.of(product));

        ProductDto productDto = productService.updateProduct(product.getSellerId(), updateProduct);

        assertAll(
                () -> assertEquals(productDto.getName(), updateProduct.getName()),
                () -> assertEquals(productDto.getPrice(), updateProduct.getPrice()),
                () -> assertEquals(productDto.getDiscount(), updateProduct.getDiscount()),
                () -> assertEquals(productDto.getStatus(), ProductStatus.valueOf(updateProduct.getStatus()))
        );
    }


    @Test
    void deleteProductSuccess() {
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        productService.deleteProduct(product.getSellerId(), product.getId());

        assertEquals(product.getStatus(), ProductStatus.DELETED);
    }

    @Test
    void getProductsByNameSuccess() {
        Product anotherProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("코카콜라 작은캔")
                .sellerId("test")
                .price(700L)
                .discount(0.0)
                .status(ProductStatus.PUBLIC)
                .updatedAt(now)
                .publishedAt(now)
                .star(4.3)
                .build();

        SearchProductByName searchProductByName = SearchProductByName.builder()
                .name("코카콜라")
                .build();

        List<Product> products = List.of(product, anotherProduct);
        given(productRepository.findAll(any(Specification.class))).willReturn(products);

        List<ProductDto> productDtos = productService.getProductsByName(searchProductByName, Pageable.unpaged());

        verify(productRepository, times(1)).findAll(any(Specification.class));
        assertEquals(products.size(), 2);
    }
}