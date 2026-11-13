package com.library.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IsbnRecordRepository extends JpaRepository<IsbnRecord, String> {

    List<IsbnRecord> findAllByOrderByIsbnAsc();
}
