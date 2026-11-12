package com.library.loan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/{bookId}/borrow")
    @Operation(summary = "Borrow a book copy on behalf of a borrower")
    public LoanResponse borrow(@PathVariable Long bookId, @Valid @RequestBody BorrowerActionRequest request) {
        return loanService.borrow(bookId, request.borrowerId());
    }

    @PostMapping("/{bookId}/return")
    @Operation(summary = "Return a borrowed book copy on behalf of the borrower who holds it")
    public LoanResponse returnBook(@PathVariable Long bookId, @Valid @RequestBody BorrowerActionRequest request) {
        return loanService.returnBook(bookId, request.borrowerId());
    }
}
