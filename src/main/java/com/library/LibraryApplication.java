package com.library;

import com.library.config.SqlServerBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SqlServerBootstrap.ensureDatabase();
        SpringApplication.run(LibraryApplication.class, args);
    }
}
