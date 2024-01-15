package com.zerobase.commerce.database.product.repository;

import com.zerobase.commerce.database.product.domain.Product;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    List<Product> findBySellerIdOrderByUpdatedAtDesc(String sellerId, Pageable pageable);

    boolean existsById(@NonNull UUID id);
}
