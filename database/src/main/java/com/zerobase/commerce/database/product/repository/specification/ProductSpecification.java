package com.zerobase.commerce.database.product.repository.specification;

import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.domain.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    public static Specification<Product> nameEquals(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Product> nameLikes(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), '%' + name + '%');
    }

    public static Specification<Product> publicOnly() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), ProductStatus.PUBLIC.name());
    }

    public static Specification<Product> orderBy(String sortBy, boolean ascending) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(ascending ? criteriaBuilder.asc(root.get(sortBy)) : criteriaBuilder.desc(root.get(sortBy)));
            return criteriaBuilder.and();
        };
    }

    public static Specification<Product> sellerIdEquals(String sellerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("sellerId"), sellerId);
    }

    public static Specification<Product> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("status"), ProductStatus.DELETED.name());
    }

    public static Specification<Product> orderByUpdatedAtDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            return criteriaBuilder.and();
        };
    }
}
