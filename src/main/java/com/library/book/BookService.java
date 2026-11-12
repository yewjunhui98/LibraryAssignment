package com.library.book;

import com.library.common.ApiException;
import com.library.common.IsbnNormalizer;
import com.library.common.TextNormalizer;
import com.library.loan.Loan;
import com.library.loan.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final IsbnRecordRepository isbnRecordRepository;
    private final LoanRepository loanRepository;
    private final TransactionTemplate newTransaction;

    public BookService(
            BookRepository bookRepository,
            IsbnRecordRepository isbnRecordRepository,
            LoanRepository loanRepository,
            PlatformTransactionManager transactionManager
    ) {
        this.bookRepository = bookRepository;
        this.isbnRecordRepository = isbnRecordRepository;
        this.loanRepository = loanRepository;
        this.newTransaction = new TransactionTemplate(transactionManager);
        this.newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Transactional
    public BookResponse register(RegisterBookRequest request) {
        String isbn = IsbnNormalizer.toIsbn13(request.isbn())
                .orElseThrow(() -> ApiException.badRequest("ISBN is not valid"));
        String title = TextNormalizer.normalize(request.title());
        String author = TextNormalizer.normalize(request.author());
        IsbnRecord record = resolveIsbn(isbn, title, author);
        Book saved = bookRepository.save(new Book(record.getIsbn(), record.getTitle(), record.getAuthor()));
        log.info("Registered book {} for ISBN {}", saved.getId(), saved.getIsbn());
        return BookResponse.of(saved, null);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> list() {
        Map<Long, Loan> activeLoans = loanRepository.findByReturnedAtIsNull().stream()
                .collect(Collectors.toMap(loan -> loan.getBook().getId(), Function.identity(), (left, right) -> left));
        return bookRepository.findAllByOrderByIdAsc().stream()
                .map(book -> BookResponse.of(book, activeLoans.get(book.getId())))
                .toList();
    }

    private IsbnRecord resolveIsbn(String isbn, String title, String author) {
        return isbnRecordRepository.findById(isbn)
                .map(existing -> {
                    ensureSameEdition(existing, title, author);
                    return existing;
                })
                .orElseGet(() -> insertIsbn(isbn, title, author));
    }

    private IsbnRecord insertIsbn(String isbn, String title, String author) {
        try {
            return newTransaction.execute(status ->
                    isbnRecordRepository.saveAndFlush(new IsbnRecord(isbn, title, author)));
        } catch (DataIntegrityViolationException ex) {
            IsbnRecord concurrent = isbnRecordRepository.findById(isbn).orElseThrow(() -> ex);
            ensureSameEdition(concurrent, title, author);
            return concurrent;
        }
    }

    private void ensureSameEdition(IsbnRecord existing, String title, String author) {
        if (!existing.getTitle().equals(title) || !existing.getAuthor().equals(author)) {
            throw ApiException.conflict(
                    "ISBN " + existing.getIsbn() + " is already registered as \""
                            + existing.getTitle() + "\" by " + existing.getAuthor()
            );
        }
    }
}
