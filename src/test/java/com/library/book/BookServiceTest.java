package com.library.book;

import com.library.common.ApiException;
import com.library.loan.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private IsbnRecordRepository isbnRecordRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, isbnRecordRepository, loanRepository, transactionManager);
    }

    @Test
    void rejectsACopyWhoseTitleOrAuthorDoesNotMatchTheIsbn() {
        when(isbnRecordRepository.findById("9780134685991"))
                .thenReturn(Optional.of(new IsbnRecord("9780134685991", "Effective Java", "Joshua Bloch")));

        ApiException exception = assertThrows(ApiException.class, () -> bookService.register(
                new RegisterBookRequest("978-0-13-468599-1", "Effective Java", "Someone Else")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(bookRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
