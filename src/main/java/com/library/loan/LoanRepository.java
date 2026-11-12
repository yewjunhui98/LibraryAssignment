package com.library.loan;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);

    Optional<Loan> findByBookIdAndReturnedAtIsNull(Long bookId);

    @EntityGraph(attributePaths = {"book", "borrower"})
    List<Loan> findByReturnedAtIsNull();
}
