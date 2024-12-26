package com.imse.onlineshop.controller.sql;

import com.imse.onlineshop.sql.services.DatabaseFiller;
import com.imse.onlineshop.sql.services.NoSQLMigrator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/functions")
public class FunctionsController {
    private final DatabaseFiller filler;
    private final NoSQLMigrator migrator;

    public FunctionsController(DatabaseFiller filler, NoSQLMigrator migrator) {
        this.filler = filler;
        this.migrator = migrator;
    }

    @PostMapping("/fillsqldatabase")
    public void fillDatabase() {
        filler.fillDatabase();
    }

    @PostMapping("/migratedatabase")
    public void migrateDatabase() {
        migrator.migrateDatabase();
    }
}