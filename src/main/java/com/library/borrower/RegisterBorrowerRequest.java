package com.library.borrower;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterBorrowerRequest(
        @Schema(example = "Ada Lovelace")
        @NotBlank
        @Size(max = 200)
        String name,
        @Schema(example = "ada@example.com")
        @NotBlank
        @Email
        @Size(max = 320)
        String email
) {
}
