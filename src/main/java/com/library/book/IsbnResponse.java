package com.library.book;

public record IsbnResponse(String isbn, String title, String author) {

    public static IsbnResponse from(IsbnRecord record) {
        return new IsbnResponse(record.getIsbn(), record.getTitle(), record.getAuthor());
    }
}
