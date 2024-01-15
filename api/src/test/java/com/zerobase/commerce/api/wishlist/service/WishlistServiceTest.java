package com.zerobase.commerce.api.wishlist.service;

import com.zerobase.commerce.api.wishlist.dto.AddWishlist;
import com.zerobase.commerce.api.wishlist.dto.UpdateWishlist;
import com.zerobase.commerce.api.wishlist.dto.WishlistDto;
import com.zerobase.commerce.database.order.repository.OrderRepository;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.user.repository.UserRepository;
import com.zerobase.commerce.database.wishlist.domain.Wishlist;
import com.zerobase.commerce.database.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private Wishlist wishlist;
    private Product product;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

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

        wishlist = Wishlist.builder()
                .id(1L)
                .userId("test")
                .productId(UUID.randomUUID())
                .amount(1L)
                .addedAt(now)
                .build();

    }

    @Test
    void addWishlist() {
        AddWishlist addWishlist = AddWishlist.builder()
                .amount(1L)
                .productId(UUID.randomUUID())
                .build();

        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        wishlistService.addWishlist("test", addWishlist);

        ArgumentCaptor<Wishlist> captor = ArgumentCaptor.forClass(Wishlist.class);

        verify(wishlistRepository, times(1))
                .save(captor.capture());

        assertAll(
                () -> assertEquals(captor.getValue().getAmount(), addWishlist.getAmount()),
                () -> assertEquals(captor.getValue().getProductId(), addWishlist.getProductId())
        );
    }

    @Test
    void updateWishlist() {
        UpdateWishlist updateWishlist = UpdateWishlist.builder()
                .wishlistId(1L)
                .amount(2L)
                .build();

        given(wishlistRepository.findById(anyLong()))
                .willReturn(Optional.of(wishlist));

        WishlistDto wishlistDto = wishlistService.updateWishlist("test", updateWishlist);

        verify(wishlistRepository, times(1)).findById(1L);

        assertEquals(wishlistDto.getId(), 1);
    }

    @Test
    void deleteWishlist() {
        given(wishlistRepository.findById(anyLong()))
                .willReturn(Optional.of(wishlist));

        wishlistService.deleteWishlist("test", 1L);

        verify(wishlistRepository, times(1)).delete(wishlist);
    }
}