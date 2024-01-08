package com.zerobase.commerce.api.product.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.ProductDto;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.database.product.constant.ProductSortFilter;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.constant.SortOrder;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.product.repository.specification.ProductSpecification;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.zerobase.commerce.database.product.constant.SortOrder.ASC;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final TokenAuthenticator tokenAuthenticator;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductDto addProduct(HttpHeaders headers, AddProduct request) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        LocalDateTime now = LocalDateTime.now();

        ProductStatus status;
        if (request.getStatus() == null) {
            status = ProductStatus.PUBLIC;
        } else {
            status = ProductStatus.valueOf(request.getStatus());
        }

        Product product = Product.builder()
                .name(request.getName())
                .sellerId(user.getId())
                .price(request.getPrice())
                .status(status)
                .discount(0.0)
                .star(0.0)
                .publishedAt(now)
                .updatedAt(now)
                .build();

        return ProductDto.fromEntity(product);
    }

    public ProductDto getProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (product.getStatus() == ProductStatus.PRIVATE)
            throw new CustomException(ErrorCode.PRIVATE_PRODUCT);

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

        // TODO: 앞으로 추가 될 Wishlist, Order, Review도 지운다?

        productRepository.delete(product);
    }

    public List<ProductDto> getProductsBySeller(HttpHeaders headers) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        return productRepository.findBySellerIdOrderByUpdatedAtDesc(user.getId())
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
