package com.library.loan;

import com.library.book.Book;
import com.library.book.BookRepository;
import com.library.borrower.Borrower;
import com.library.borrower.BorrowerRepository;
import com.library.common.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void borrowsAnAvailableCopy() {
        Book book = new Book(5L, "9780134685991", "Effective Java", "Joshua Bloch");
        Borrower borrower = new Borrower(3L, "Ada Lovelace", "ada@example.com");
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(3L)).thenReturn(Optional.of(borrower));
        when(loanRepository.existsByBookIdAndReturnedAtIsNull(5L)).thenReturn(false);
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = loanService.borrow(5L, 3L);

        assertEquals(5L, response.bookId());
        assertEquals(3L, response.borrowerId());
        assertNull(response.returnedAt());
    }

    @Test
    void rejectsASecondBorrowerForTheSameCopy() {
        when(bookRepository.findById(5L)).thenReturn(Optional.of(new Book(5L, "9780134685991", "Effective Java", "Joshua Bloch")));
        when(borrowerRepository.findById(9L)).thenReturn(Optional.of(new Borrower(9L, "Grace Hopper", "grace@example.com")));
        when(loanRepository.existsByBookIdAndReturnedAtIsNull(5L)).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> loanService.borrow(5L, 9L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void rejectsReturnFromABorrowerWhoDoesNotHoldTheCopy() {
        Loan loan = new Loan(
                new Book(5L, "9780134685991", "Effective Java", "Joshua Bloch"),
                new Borrower(3L, "Ada Lovelace", "ada@example.com"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
        when(bookRepository.existsById(5L)).thenReturn(true);
        when(borrowerRepository.existsById(9L)).thenReturn(true);
        when(loanRepository.findByBookIdAndReturnedAtIsNull(5L)).thenReturn(Optional.of(loan));

        ApiException exception = assertThrows(ApiException.class, () -> loanService.returnBook(5L, 9L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertNull(loan.getReturnedAt());
    }
}
