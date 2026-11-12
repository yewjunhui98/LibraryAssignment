package com.library.book;

import com.library.common.ValidIsbn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterBookRequest(
        @Schema(example = "978-0-13-468599-1")
        @NotBlank
        @Size(max = 32)
        @ValidIsbn
        String isbn,
        @Schema(example = "Effective Java")
        @NotBlank
        @Size(max = 500)
        String title,
        @Schema(example = "Joshua Bloch")
        @NotBlank
        @Size(max = 300)
        String author
) {
}
