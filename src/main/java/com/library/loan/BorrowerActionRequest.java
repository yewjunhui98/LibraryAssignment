package com.library.loan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BorrowerActionRequest(
        @Schema(example = "1")
        @NotNull
        @Positive
        Long borrowerId
) {
}
