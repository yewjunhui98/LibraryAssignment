package com.library.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlServerBootstrapTest {

    @Test
    void pointsTheStartupConnectionAtMaster() {
        assertEquals(
                "jdbc:sqlserver://sqlserver:1433;databaseName=master;encrypt=true;trustServerCertificate=true",
                SqlServerBootstrap.masterUrl(
                        "jdbc:sqlserver://sqlserver:1433;databaseName=library;encrypt=true;trustServerCertificate=true")
        );
    }
}
