package com.library.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IsbnRecordRepository extends JpaRepository<IsbnRecord, String> {
}
