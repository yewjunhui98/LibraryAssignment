package com.library.borrower;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {

    boolean existsByEmail(String email);

    Optional<Borrower> findByEmail(String email);
}
