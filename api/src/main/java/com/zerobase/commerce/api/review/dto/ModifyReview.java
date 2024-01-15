package com.zerobase.commerce.api.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyReview {
    @NotNull(message = "Id must not be null")
    private Long id;

    @Range(max = 5L)
    private Double star;
}
