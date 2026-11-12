CREATE TABLE borrowers (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    name NVARCHAR(200) NOT NULL,
    email NVARCHAR(320) NOT NULL,
    CONSTRAINT uq_borrowers_email UNIQUE (email)
);

CREATE TABLE isbn_records (
    isbn NVARCHAR(13) NOT NULL PRIMARY KEY,
    title NVARCHAR(500) NOT NULL,
    author NVARCHAR(300) NOT NULL
);

CREATE TABLE books (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    isbn NVARCHAR(13) NOT NULL,
    title NVARCHAR(500) NOT NULL,
    author NVARCHAR(300) NOT NULL,
    CONSTRAINT fk_books_isbn FOREIGN KEY (isbn) REFERENCES isbn_records (isbn)
);

CREATE INDEX ix_books_isbn ON books (isbn);

CREATE TABLE loans (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    borrower_id BIGINT NOT NULL,
    borrowed_at DATETIME2(6) NOT NULL,
    returned_at DATETIME2(6) NULL,
    CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_loans_borrower FOREIGN KEY (borrower_id) REFERENCES borrowers (id)
);

CREATE UNIQUE INDEX uq_loans_one_active_per_book
    ON loans (book_id)
    WHERE returned_at IS NULL;
