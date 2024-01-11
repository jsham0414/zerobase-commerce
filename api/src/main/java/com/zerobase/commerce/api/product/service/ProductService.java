package com.zerobase.commerce.api.product.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.ProductDto;
import com.zerobase.commerce.api.product.dto.ProductRanking;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.database.product.constant.ProductSortFilter;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.constant.SortOrder;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.product.repository.specification.ProductSpecification;
import com.zerobase.commerce.database.review.domain.Review;
import com.zerobase.commerce.database.review.repository.ReviewRepository;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import com.zerobase.commerce.database.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.zerobase.commerce.database.product.constant.SortOrder.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final TokenAuthenticator tokenAuthenticator;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final WishlistRepository wishlistRepository;

    private final String starKey = "star";

    @Transactional
    @Scheduled(cron = "${scheduler.review.update}")
    void updateStar() throws InterruptedException {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            double sum = 0.0;



            List<Review> reviews = reviewRepository.findByProductId(product.getId());
            if (reviews.isEmpty()) {
                continue;
            }

            for (Review review : reviews) {
                sum += review.getStar();
            }

            product.setStar(sum / reviews.size());

            String key = String.format("%s:%s", product.getId(), product.getName());
            if (product.getStatus() != ProductStatus.PUBLIC && redisTemplate.boundZSetOps(starKey).score(key) != null) {
                redisTemplate.opsForZSet().remove(starKey, key);
            } else if (product.getStatus() == ProductStatus.PUBLIC) {
                redisTemplate.opsForZSet().add(starKey, key, product.getStar());
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
    public ProductDto addProduct(HttpHeaders headers, AddProduct request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);

        LocalDateTime now = LocalDateTime.now();

        ProductStatus status;
        if (request.getStatus() == null) {
            status = ProductStatus.PUBLIC;
        } else {
            status = ProductStatus.valueOf(request.getStatus());
        }

        Product product = Product.builder()
                .name(request.getName())
                .sellerId(userId)
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
    public ProductDto updateProduct(HttpHeaders headers, UpdateProduct request) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Product product = productRepository.findById(request.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(product.getSellerId(), user.getId())) {
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

        productRepository.save(product);

        return ProductDto.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(HttpHeaders headers, UUID productId) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(product.getSellerId(), user.getId())) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        if (Objects.equals(product.getStatus(), ProductStatus.DELETED)) {
            throw new CustomException(ErrorCode.DELETED_PRODUCT);
        }

        wishlistRepository.deleteAllByProductId(productId);

        product.setStatus(ProductStatus.DELETED);
    }

    public List<ProductDto> getProductsBySeller(HttpHeaders headers) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);

        Specification<Product> specification = Specification
                .where(ProductSpecification.sellerIdEquals(id))
                .and(ProductSpecification.notDeleted())
                .and(ProductSpecification.orderByUpdatedAtDesc());

        return productRepository.findAll(specification)
                .stream()
                .map(ProductDto::fromEntity)
                .toList();
    }

    public List<ProductDto> getProductsByName(String name, String filter, String order) {
        if ((filter == null) != (order == null))
            throw new CustomException(ErrorCode.BAD_REQUEST);

        Specification<Product> specification = Specification
                .where(ProductSpecification.nameEquals(name))
                .and(ProductSpecification.publicOnly());

        if (filter != null) {
            ProductSortFilter productSortFilter = ProductSortFilter.valueOf(filter);
            SortOrder sortOrder = SortOrder.valueOf(order);

            specification.and(ProductSpecification.orderBy(productSortFilter.name(), sortOrder == ASC));
        }

        return productRepository.findAll(specification)
                .stream()
                .map(ProductDto::fromEntity)
                .toList();
    }
}
