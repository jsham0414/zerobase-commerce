package com.zerobase.commerce.api.product.dto;

import lombok.*;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRanking {
    private UUID id;
    private String name;
    private Double star;

    public static ProductRanking fromTuple(ZSetOperations.TypedTuple<String> tuple) {
        String[] values = Objects.requireNonNull(tuple.getValue()).split(":");
        return ProductRanking.builder()
                .id(UUID.fromString(values[0]))
                .name(values[1])
                .star(tuple.getScore())
                .build();
    }
}
