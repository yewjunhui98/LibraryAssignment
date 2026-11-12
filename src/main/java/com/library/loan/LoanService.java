package com.library.loan;

import com.library.book.Book;
import com.library.book.BookRepository;
import com.library.borrower.Borrower;
import com.library.borrower.BorrowerRepository;
import com.library.common.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;

    public LoanService(
            BookRepository bookRepository,
            BorrowerRepository borrowerRepository,
            LoanRepository loanRepository
    ) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public LoanResponse borrow(Long bookId, Long borrowerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> ApiException.notFound("Book " + bookId + " was not found"));
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> ApiException.notFound("Borrower " + borrowerId + " was not found"));
        if (loanRepository.existsByBookIdAndReturnedAtIsNull(bookId)) {
            throw ApiException.conflict("Book " + bookId + " is already borrowed");
        }
        Loan loan = loanRepository.save(new Loan(book, borrower, Instant.now()));
        log.info("Borrower {} borrowed book {}", borrowerId, bookId);
        return LoanResponse.from(loan);
    }

    @Transactional
    public LoanResponse returnBook(Long bookId, Long borrowerId) {
        if (!bookRepository.existsById(bookId)) {
            throw ApiException.notFound("Book " + bookId + " was not found");
        }
        if (!borrowerRepository.existsById(borrowerId)) {
            throw ApiException.notFound("Borrower " + borrowerId + " was not found");
        }
        Loan loan = loanRepository.findByBookIdAndReturnedAtIsNull(bookId)
                .orElseThrow(() -> ApiException.conflict("Book " + bookId + " is not currently borrowed"));
        if (!loan.getBorrower().getId().equals(borrowerId)) {
            throw ApiException.conflict("Book " + bookId + " is borrowed by a different borrower");
        }
        loan.setReturnedAt(Instant.now());
        log.info("Borrower {} returned book {}", borrowerId, bookId);
        return LoanResponse.from(loan);
    }
}
