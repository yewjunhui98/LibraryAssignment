package com.library.borrower;

import com.library.common.ApiException;
import com.library.common.TextNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BorrowerService {

    private static final Logger log = LoggerFactory.getLogger(BorrowerService.class);

    private final BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    @Transactional
    public BorrowerResponse register(RegisterBorrowerRequest request) {
        String name = TextNormalizer.normalize(request.name());
        String email = TextNormalizer.normalizeEmail(request.email());
        if (borrowerRepository.existsByEmail(email)) {
            throw ApiException.conflict("A borrower with email " + email + " is already registered");
        }
        Borrower saved = borrowerRepository.save(new Borrower(name, email));
        log.info("Registered borrower {}", saved.getId());
        return BorrowerResponse.from(saved);
    }
}
