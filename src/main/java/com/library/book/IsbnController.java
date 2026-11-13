package com.library.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/isbns")
@Tag(name = "ISBNs")
public class IsbnController {

    private final IsbnRecordRepository isbnRecordRepository;

    public IsbnController(IsbnRecordRepository isbnRecordRepository) {
        this.isbnRecordRepository = isbnRecordRepository;
    }

    @GetMapping
    @Operation(summary = "List every ISBN registered in the library")
    public List<IsbnResponse> list() {
        return isbnRecordRepository.findAllByOrderByIsbnAsc().stream()
                .map(IsbnResponse::from)
                .toList();
    }
}
