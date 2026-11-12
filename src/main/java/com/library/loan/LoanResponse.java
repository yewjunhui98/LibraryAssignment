package com.library.loan;

import java.time.Instant;

public record LoanResponse(
        Long id,
        Long bookId,
        Long borrowerId,
        Instant borrowedAt,
        Instant returnedAt
) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBorrower().getId(),
                loan.getBorrowedAt(),
                loan.getReturnedAt()
        );
    }
}
