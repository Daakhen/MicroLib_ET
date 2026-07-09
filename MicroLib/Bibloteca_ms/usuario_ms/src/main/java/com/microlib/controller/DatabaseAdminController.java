package com.microlib.controller;

import org.flywaydb.core.Flyway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/db")
public class DatabaseAdminController {

    private final Flyway flyway;

    public DatabaseAdminController(Flyway flyway) {
        this.flyway = flyway;
    }

    @PostMapping("/repair")
    public String repairDatabase() {
        flyway.repair();
        return "Historial de Flyway reparado. Ya puedes reintentar la migración.";
    }
}