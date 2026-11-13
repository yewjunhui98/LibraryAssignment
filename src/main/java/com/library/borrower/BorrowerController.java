package com.library.borrower;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
@Tag(name = "Borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new borrower")
    public BorrowerResponse register(@Valid @RequestBody RegisterBorrowerRequest request) {
        return borrowerService.register(request);
    }

    @GetMapping
    @Operation(summary = "List every borrower")
    public List<BorrowerResponse> list() {
        return borrowerService.list();
    }
}
