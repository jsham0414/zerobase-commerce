package com.zerobase.commerce.api.product.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.product.dto.*;
import com.zerobase.commerce.database.product.constant.ProductSortFilter;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.constant.SortOrder;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.product.repository.specification.ProductSpecification;
import com.zerobase.commerce.database.review.domain.Review;
import com.zerobase.commerce.database.review.repository.ReviewRepository;
import com.zerobase.commerce.database.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.zerobase.commerce.database.product.constant.SortOrder.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final WishlistRepository wishlistRepository;

    private final String starKey = "star";

    @Transactional
    @Scheduled(cron = "${scheduler.review.test_update}")
    void updateStar() throws InterruptedException {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            double sum = 0.0;

            List<Review> reviews = reviewRepository.findByProductId(product.getId(), null);
            if (reviews.isEmpty()) {
                continue;
            }

            for (Review review : reviews) {
                sum += review.getStar();
            }

            product.setStar(sum / reviews.size());

            String key = String.format("%s:%s", product.getId(), product.getName());

            try {
                if (product.getStatus() != ProductStatus.PUBLIC && redisTemplate.boundZSetOps(starKey).score(key) != null) {
                    redisTemplate.opsForZSet().remove(starKey, key);
                } else if (product.getStatus() == ProductStatus.PUBLIC) {
                    redisTemplate.opsForZSet().add(starKey, key, product.getStar());
                }
            } catch (RedisConnectionFailureException e) {
                e.printStackTrace();
            }

            productRepository.save(product);

            Thread.sleep(1000);
        }
    }

    public List<ProductRanking> getStarRanking() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(starKey, 0, 10);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Collections.emptyList();
        }

        return typedTuples
                .stream()
                .map(ProductRanking::fromTuple)
                .toList();
    }

    @Transactional
    public ProductDto addProduct(String sellerId, AddProduct request) {
        LocalDateTime now = LocalDateTime.now();

        ProductStatus status;
        if (request.getStatus() == null) {
            status = ProductStatus.PUBLIC;
        } else {
            status = ProductStatus.valueOf(request.getStatus());
        }

        Product product = Product.builder()
                .name(request.getName())
                .sellerId(sellerId)
                .price(request.getPrice())
                .status(status)
                .discount(0.0)
                .star(0.0)
                .publishedAt(now)
                .updatedAt(now)
                .build();

        return ProductDto.fromEntity(productRepository.save(product));
    }

    public ProductDto getProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (product.getStatus() == ProductStatus.PRIVATE) {
            throw new CustomException(ErrorCode.PRIVATE_PRODUCT);
        }

        if (Objects.equals(product.getStatus(), ProductStatus.DELETED)) {
            throw new CustomException(ErrorCode.DELETED_PRODUCT);
        }

        return ProductDto.fromEntity(product);
    }

    @Transactional
    public ProductDto updateProduct(String sellerId, UpdateProduct request) {
        Product product = productRepository.findById(request.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(product.getSellerId(), sellerId)) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        if (Objects.equals(product.getStatus(), ProductStatus.DELETED)) {
            throw new CustomException(ErrorCode.DELETED_PRODUCT);
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getStatus() != null) {
            product.setStatus(ProductStatus.valueOf(request.getStatus()));
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDiscount() != null) {
            product.setDiscount(request.getDiscount());
        }

        return ProductDto.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(String sellerId, UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(product.getSellerId(), sellerId)) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        if (Objects.equals(product.getStatus(), ProductStatus.DELETED)) {
            throw new CustomException(ErrorCode.DELETED_PRODUCT);
        }

        wishlistRepository.deleteAllByProductId(productId);

        product.setStatus(ProductStatus.DELETED);
    }

    public List<ProductDto> getProductsBySeller(String sellerId, Pageable pageable) {
        Specification<Product> specification = Specification
                .where(ProductSpecification.sellerIdEquals(sellerId))
                .and(ProductSpecification.notDeleted())
                .and(ProductSpecification.orderByUpdatedAtDesc());

        return productRepository.findAll(specification, pageable)
                .stream()
                .map(ProductDto::fromEntity)
                .toList();
    }

    public List<ProductDto> getProductsByName(SearchProductByName request, Pageable pageable) {
        if ((request.getFilter() == null) != (request.getOrder() == null))
            throw new CustomException(ErrorCode.BAD_REQUEST);

        Specification<Product> specification = Specification
                .where(ProductSpecification.nameLikes(request.getName()))
                .and(ProductSpecification.publicOnly());

        if (request.getFilter() != null) {
            ProductSortFilter productSortFilter = ProductSortFilter.valueOf(request.getFilter());
            SortOrder sortOrder = SortOrder.valueOf(request.getOrder());

            specification.and(ProductSpecification.orderBy(productSortFilter.name(), sortOrder == ASC));
        }

        return productRepository.findAll(specification, pageable)
                .stream()
                .map(ProductDto::fromEntity)
                .toList();
    }
}
