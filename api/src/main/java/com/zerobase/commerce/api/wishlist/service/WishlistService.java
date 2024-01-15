package com.zerobase.commerce.api.wishlist.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public WishlistDto addWishlist(String userId, AddWishlist request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new CustomException(ErrorCode.DELETED_PRODUCT);
        }

        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .productId(request.getProductId())
                .amount(request.getAmount())
                .addedAt(LocalDateTime.now())
                .build();

        return WishlistDto.fromEntity(wishlistRepository.save(wishlist));
    }

    public List<WishlistDto> getWishlist(String userId, Pageable pageable) {
        return wishlistRepository.findByUserIdOrderByAddedAtDesc(userId, pageable)
                .stream()
                .map(WishlistDto::fromEntity)
                .toList();
    }

    @Transactional
    public WishlistDto updateWishlist(String userId, UpdateWishlist request) {
        Wishlist wishlist = wishlistRepository.findById(request.getWishlistId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_WISHLIST_ID)
        );

        if (!Objects.equals(wishlist.getUserId(), userId)) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        if (request.getAmount() != null) {
            wishlist.setAmount(request.getAmount());
        }

        return WishlistDto.fromEntity(wishlist);
    }

    @Transactional
    public void deleteWishlist(String userId, Long id) {
        Wishlist wishlist = wishlistRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_WISHLIST_ID)
        );

        if (!Objects.equals(wishlist.getUserId(), userId)) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        wishlistRepository.delete(wishlist);
    }

}
