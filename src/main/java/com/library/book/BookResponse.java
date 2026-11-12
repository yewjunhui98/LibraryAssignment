package com.library.book;

import com.library.borrower.BorrowerResponse;
import com.library.loan.Loan;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        BookStatus status,
        BorrowerResponse borrowedBy
) {

    public static BookResponse of(Book book, Loan activeLoan) {
        if (activeLoan == null) {
            return new BookResponse(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), BookStatus.AVAILABLE, null);
        }
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                BookStatus.BORROWED,
                BorrowerResponse.from(activeLoan.getBorrower())
        );
    }
}
