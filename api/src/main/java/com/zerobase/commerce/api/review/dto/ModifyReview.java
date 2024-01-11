package com.zerobase.commerce.api.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ModifyReview {
    @NotNull(message = "Id must not be null")
    private Long id;

    @Range(max = 5L)
    private Double star;
}
