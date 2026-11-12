package com.library.borrower;

public record BorrowerResponse(Long id, String name, String email) {

    public static BorrowerResponse from(Borrower borrower) {
        return new BorrowerResponse(borrower.getId(), borrower.getName(), borrower.getEmail());
    }
}
